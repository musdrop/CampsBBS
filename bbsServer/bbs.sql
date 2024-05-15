create table comment
(
    id          int auto_increment
        primary key,
    forum_id    int          not null comment '所处的帖子id',
    user_id     int          not null comment '发布评论的用户id',
    content     varchar(255) not null comment '评论内容',
    create_time timestamp    not null comment '创建时间'
)
    comment '评论';

create table forum
(
    id          int auto_increment
        primary key,
    title       varchar(255)  not null comment '标题',
    cover_image varchar(255)  not null comment '封面图',
    user_id     int           not null comment '发布用户的id',
    content     varchar(1000) null comment '正文内容',
    delete_flag int           not null comment '删除标记，0正常，1删除',
    create_time timestamp     not null comment '创建时间',
    update_time timestamp     not null comment '修改时间'
)
    comment '帖子';

create table user
(
    id          int auto_increment
        primary key,
    account     varchar(255) not null comment '账号',
    name        varchar(255) not null comment '名称',
    password    varchar(255) not null comment '密码',
    mail        varchar(255) not null comment '邮箱',
    head        varchar(255) not null comment '头像文件名称',
    auth        int          not null comment '权限登记，0用户，1管理员',
    create_time timestamp    not null comment '创建时间',
    update_time timestamp    not null comment '修改时间'
)
    comment '用户信息表';


