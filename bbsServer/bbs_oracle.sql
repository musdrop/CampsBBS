-- Create sequences for auto-increment
CREATE SEQUENCE comment_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE forum_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

-- Comment table
CREATE TABLE "bbs_comment" (
                         id          NUMBER PRIMARY KEY,
                         forum_id    NUMBER NOT NULL,
                         user_id     NUMBER NOT NULL,
                         content     VARCHAR2(255) NOT NULL,
                         create_time TIMESTAMP NOT NULL
);

-- Add comment
COMMENT ON TABLE bbs_comment IS '评论';
COMMENT ON COLUMN bbs_comment.forum_id IS '所处的帖子id';
COMMENT ON COLUMN bbs_comment.user_id IS '发布评论的用户id';
COMMENT ON COLUMN bbs_comment.content IS '评论内容';
COMMENT ON COLUMN bbs_comment.create_time IS '创建时间';

-- Auto-increment trigger for comment
CREATE OR REPLACE TRIGGER comment_id_trigger
    BEFORE INSERT ON bbs_comment
    FOR EACH ROW
BEGIN
    SELECT comment_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- Forum table
CREATE TABLE bbs_forum (
                       id          NUMBER PRIMARY KEY,
                       title       VARCHAR2(255) NOT NULL,
                       cover_image VARCHAR2(255) NOT NULL,
                       user_id     NUMBER NOT NULL,
                       content     VARCHAR2(1000),
                       delete_flag NUMBER NOT NULL,
                       create_time TIMESTAMP NOT NULL,
                       update_time TIMESTAMP NOT NULL
);

-- Add comment
COMMENT ON TABLE bbs_forum IS '帖子';
COMMENT ON COLUMN bbs_forum.title IS '标题';
COMMENT ON COLUMN bbs_forum.cover_image IS '封面图';
COMMENT ON COLUMN bbs_forum.user_id IS '发布用户的id';
COMMENT ON COLUMN bbs_forum.content IS '正文内容';
COMMENT ON COLUMN bbs_forum.delete_flag IS '删除标记，0正常，1删除';
COMMENT ON COLUMN bbs_forum.create_time IS '创建时间';
COMMENT ON COLUMN bbs_forum.update_time IS '修改时间';

-- Auto-increment trigger for forum
CREATE OR REPLACE TRIGGER forum_id_trigger
    BEFORE INSERT ON bbs_forum
    FOR EACH ROW
BEGIN
    SELECT forum_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- User table
CREATE TABLE "bbs_user" (
                        id          NUMBER PRIMARY KEY,
                        account     VARCHAR2(255) NOT NULL,
                        name        VARCHAR2(255) NOT NULL,
                        password    VARCHAR2(255) NOT NULL,
                        mail        VARCHAR2(255) NOT NULL,
                        head        VARCHAR2(255) NOT NULL,
                        auth        NUMBER NOT NULL,
                        create_time TIMESTAMP NOT NULL,
                        update_time TIMESTAMP NOT NULL
);

-- Add comment
COMMENT ON TABLE bbs_user IS '用户信息表';
COMMENT ON COLUMN bbs_user.account IS '账号';
COMMENT ON COLUMN bbs_user.name IS '名称';
COMMENT ON COLUMN bbs_user.password IS '密码';
COMMENT ON COLUMN bbs_user.mail IS '邮箱';
COMMENT ON COLUMN bbs_user.head IS '头像文件名称';
COMMENT ON COLUMN bbs_user.auth IS '权限登记，0用户，1管理员';
COMMENT ON COLUMN bbs_user.create_time IS '创建时间';
COMMENT ON COLUMN bbs_user.update_time IS '修改时间';

-- Auto-increment trigger for user
CREATE OR REPLACE TRIGGER user_id_trigger
    BEFORE INSERT ON bbs_user
    FOR EACH ROW
BEGIN
    SELECT user_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/