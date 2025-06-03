create sequence COMMENT_SEQ
/

create sequence FORUM_SEQ
/

create sequence USER_SEQ
/

create sequence LIKE_SEQ
/

create table BBS_COMMENT
(
    ID          NUMBER        not null
        primary key,
    FORUM_ID    NUMBER        not null,
    USER_ID     NUMBER        not null,
    CONTENT     VARCHAR2(255) not null,
    CREATE_TIME TIMESTAMP(6)  not null,
    PARENT_ID   NUMBER default 0,
    DELETE_FLAG NUMBER default 0,
    UPDATE_TIME TIMESTAMP(6),
    LIKE_COUNT  NUMBER default 0
)
/

comment on table BBS_COMMENT is '评论'
/

comment on column BBS_COMMENT.FORUM_ID is '所处的帖子id'
/

comment on column BBS_COMMENT.USER_ID is '发布评论的用户id'
/

comment on column BBS_COMMENT.CONTENT is '评论内容'
/

comment on column BBS_COMMENT.CREATE_TIME is '创建时间'
/

create trigger COMMENT_ID_TRIGGER
    before insert
    on BBS_COMMENT
    for each row
BEGIN
    SELECT comment_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

create table BBS_FORUM
(
    ID            NUMBER        not null
        primary key,
    TITLE         VARCHAR2(255) not null,
    COVER_IMAGE   VARCHAR2(255) not null,
    USER_ID       NUMBER        not null,
    CONTENT       VARCHAR2(1000),
    DELETE_FLAG   NUMBER        not null,
    CREATE_TIME   TIMESTAMP(6)  not null,
    UPDATE_TIME   TIMESTAMP(6)  not null,
    VIEW_COUNT    NUMBER default 0,
    LIKE_COUNT    NUMBER default 0,
    COMMENT_COUNT NUMBER default 0
)
/

comment on table BBS_FORUM is '帖子'
/

comment on column BBS_FORUM.TITLE is '标题'
/

comment on column BBS_FORUM.COVER_IMAGE is '封面图'
/

comment on column BBS_FORUM.USER_ID is '发布用户的id'
/

comment on column BBS_FORUM.CONTENT is '正文内容'
/

comment on column BBS_FORUM.DELETE_FLAG is '删除标记，0正常，1删除'
/

comment on column BBS_FORUM.CREATE_TIME is '创建时间'
/

comment on column BBS_FORUM.UPDATE_TIME is '修改时间'
/

create trigger FORUM_ID_TRIGGER
    before insert
    on BBS_FORUM
    for each row
BEGIN
    SELECT forum_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

create table BBS_USER
(
    ID          NUMBER        not null
        primary key,
    ACCOUNT     VARCHAR2(255) not null,
    NAME        VARCHAR2(255) not null,
    PASSWORD    VARCHAR2(255) not null,
    MAIL        VARCHAR2(255) not null,
    HEAD        VARCHAR2(255) not null,
    AUTH        NUMBER        not null,
    CREATE_TIME TIMESTAMP(6)  not null,
    UPDATE_TIME TIMESTAMP(6)  not null
)
/

comment on table BBS_USER is '用户信息表'
/

comment on column BBS_USER.ACCOUNT is '账号'
/

comment on column BBS_USER.NAME is '名称'
/

comment on column BBS_USER.PASSWORD is '密码'
/

comment on column BBS_USER.MAIL is '邮箱'
/

comment on column BBS_USER.HEAD is '头像文件名称'
/

comment on column BBS_USER.AUTH is '权限登记，0用户，1管理员'
/

comment on column BBS_USER.CREATE_TIME is '创建时间'
/

comment on column BBS_USER.UPDATE_TIME is '修改时间'
/

create trigger USER_ID_TRIGGER
    before insert
    on BBS_USER
    for each row
BEGIN
    SELECT user_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

create table BBS_LIKE
(
    ID          NUMBER not null
        constraint ID
            primary key,
    TYPE        NUMBER,
    TARGET_ID   NUMBER,
    USER_ID     NUMBER,
    STATUS      NUMBER default 1,
    CREATE_TIME TIMESTAMP(6),
    UPDATE_TIME TIMESTAMP(6)
)
/

create trigger LIKE_ID_TRIGGER
    before insert
    on BBS_LIKE
    for each row
BEGIN
    SELECT like_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

