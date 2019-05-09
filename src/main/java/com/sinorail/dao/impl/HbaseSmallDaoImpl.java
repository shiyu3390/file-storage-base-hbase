package com.sinorail.dao.impl;

import com.sinorail.common.Binary;
import com.sinorail.common.Contansts;
import com.sinorail.dao.SmallFileDao;
import com.sinorail.domain.SmallFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: servlet-photo-storage-system
 * @BelongsPackage: com.sinorail.dao.impl
 * @Author: shiyu
 * @CreateTime: 2019-05-05
 */
@Repository
public class HbaseSmallDaoImpl implements SmallFileDao {
    private Logger logger = LoggerFactory.getLogger(HbaseSmallDaoImpl.class);

    @Autowired
    private HbaseTemplate hbaseTemplate;

    private final String encoding = "utf-8";

    private void createTableIfNotExists(TableName tableName) {
        Connection connection = null;
        Admin admin = null;
        try {
            connection = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration());
            admin = connection.getAdmin();
            String[] split = tableName.toString().split(":");
            String namespace = split[0];
            NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
            List<String> namespaceList = new ArrayList<>();
            for (int i = 0; i < namespaceDescriptors.length; i++) {
                namespaceList.add(namespaceDescriptors[i].getName());
            }
            if (!namespaceList.contains(namespace)) {
                NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(namespace).build();
                admin.createNamespace(namespaceDescriptor);
            }
            if (!admin.tableExists(tableName)) {
                HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(Contansts.COLUMN_FAMILY_NAME);
                tableDescriptor.addFamily(columnDescriptor);
                admin.createTable(tableDescriptor);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public boolean save(SmallFile smallFile) {
        TableName tableName = TableName.valueOf(Contansts.TABLE_NAME);
        if (smallFile.getNameSpace() != null && !"".equals(smallFile.getNameSpace().trim())) {
            tableName = TableName.valueOf(smallFile.getNameSpace(), Contansts.TABLE_NAME);
        }
        createTableIfNotExists(tableName);
        String rowKey = DigestUtils.md5Hex(smallFile.getName());
        byte[] cf1s = Bytes.toBytes(Contansts.COLUMN_FAMILY_NAME);
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(cf1s, Bytes.toBytes("name"), Bytes.toBytes(smallFile.getName()));
        put.addColumn(cf1s, Bytes.toBytes("contentType"), Bytes.toBytes(smallFile.getContentType()));
        put.addColumn(cf1s, Bytes.toBytes("size"), Bytes.toBytes(smallFile.getSize()));
        put.addColumn(cf1s, Bytes.toBytes("uploadDate"), Bytes.toBytes(smallFile.getUploadDate().toString()));
        put.addColumn(cf1s, Bytes.toBytes("content"), smallFile.getContent().getData());
        Boolean result = false;
        try {
            result = hbaseTemplate.execute(tableName.toString(), new TableCallback<Boolean>() {
                @Override
                public Boolean doInTable(HTableInterface table) throws Throwable {
                    table.put(put);
                    return true;
                }
            });
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean saveFiles(List<SmallFile> smallFiles) {
        TableName tableName = TableName.valueOf(Contansts.TABLE_NAME);
        if (smallFiles.size() > 0) {
            SmallFile smallFile = smallFiles.get(0);
            if (smallFile.getNameSpace() != null && !"".equals(smallFile.getNameSpace().trim())) {
                tableName = TableName.valueOf(smallFile.getNameSpace(), Contansts.TABLE_NAME);
            }
        }
        createTableIfNotExists(tableName);
        byte[] cf1s = Bytes.toBytes(Contansts.COLUMN_FAMILY_NAME);
        List<Put> puts = new ArrayList<>();
        for (SmallFile smallFile : smallFiles) {
            String rowKey = DigestUtils.md5Hex(smallFile.getName());
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(cf1s, Bytes.toBytes("name"), Bytes.toBytes(smallFile.getName()));
            put.addColumn(cf1s, Bytes.toBytes("contentType"), Bytes.toBytes(smallFile.getContentType()));
            put.addColumn(cf1s, Bytes.toBytes("size"), Bytes.toBytes(smallFile.getSize()));
            put.addColumn(cf1s, Bytes.toBytes("uploadDate"), Bytes.toBytes(smallFile.getUploadDate().toString()));
            put.addColumn(cf1s, Bytes.toBytes("content"), smallFile.getContent().getData());
            puts.add(put);
        }
        Boolean result = false;
        try {
            result = hbaseTemplate.execute(tableName.toString(), new TableCallback<Boolean>() {
                @Override
                public Boolean doInTable(HTableInterface table) throws Throwable {
                    table.put(puts);
                    return true;
                }
            });
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void delete(String namespace, String id) {
        TableName tableName = TableName.valueOf(Contansts.TABLE_NAME);
        if (namespace != null && !"".equals(namespace.trim())) {
            tableName = TableName.valueOf(namespace, Contansts.TABLE_NAME);
        }
        hbaseTemplate.execute(tableName.toString(), new TableCallback<Object>() {
            @Override
            public Object doInTable(HTableInterface table) throws Throwable {
                table.delete(new Delete(Bytes.toBytes(id)));
                return null;
            }
        });
    }

    @Override
    public SmallFile find(String namespace, String id) {
        TableName tableName = TableName.valueOf(Contansts.TABLE_NAME);
        if (namespace != null && !"".equals(namespace.trim())) {
            tableName = TableName.valueOf(namespace, Contansts.TABLE_NAME);
        }
        SmallFile smallFile = new SmallFile();
        try {
            Result result = hbaseTemplate.execute(tableName.toString(), new TableCallback<Result>() {
                @Override
                public Result doInTable(HTableInterface table) throws Throwable {
                    Result result = table.get(new Get(Bytes.toBytes(id)));
                    return result;
                }
            });
            CellScanner scanner = result.cellScanner();
            while (scanner.advance()) {
                Cell current = scanner.current();
                byte[] qualifier = CellUtil.cloneQualifier(current);
                byte[] value = CellUtil.cloneValue(current);
                if ("name".equals(new String(qualifier))) {
                    smallFile.setName(new String(value));
                } else if ("content".equals(new String(qualifier))) {
                    smallFile.setContent(new Binary(value));
                } else if ("contentType".equals(new String(qualifier))) {
                    smallFile.setContentType(new String(value));
                }
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        return smallFile;
    }
}