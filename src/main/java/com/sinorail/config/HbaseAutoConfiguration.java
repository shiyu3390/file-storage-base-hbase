package com.sinorail.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * desc： hbase auto configuration
 * date： 2016-11-16 11:11:27
 */
@org.springframework.context.annotation.Configuration
//@Component
public class HbaseAutoConfiguration {

    private static final String HBASE_QUORUM = "hbase.zookeeper.quorum";
    private static final String HBASE_ROOTDIR = "hbase.rootdir";
    @Value("${spring.data.hbase.quorum}")
    private String quorum;
    @Value("${spring.data.hbase.rootDir}")
    private String rootDir;

    @Bean
    @ConditionalOnMissingBean(HbaseTemplate.class)
    public HbaseTemplate hbaseTemplatex() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set(HBASE_QUORUM, this.quorum);
        configuration.set(HBASE_ROOTDIR, this.rootDir);
        configuration.set("hbase.cluster.distributed", "true");
        configuration.set("zookeeper.session.timeout", "120000");
        configuration.set("dfs.client.socket-timeout", "120000");
        return new HbaseTemplate(configuration);
    }
}
