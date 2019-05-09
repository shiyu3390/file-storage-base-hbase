create table  if not exists file_info
(
  id           varchar(64)        not null primary key comment '编号',
  name         varchar(64)        not null comment '名称',
  content_type varchar(32)        null comment '媒体内容类型',
  size         bigint default '0' not null comment '文件流尺寸字节',
  upload_date  datetime           null comment '上传时间',
  md5          varchar(128)       null comment '文件内容摘要信息',
  content      blob               null comment '文件内容'
);