-- Comment table
CREATE TABLE bbs_comment (
                             id          INTEGER PRIMARY KEY AUTOINCREMENT,
                             forum_id    INTEGER NOT NULL, -- 所处的帖子id
                             user_id     INTEGER NOT NULL, -- 发布评论的用户id
                             content     TEXT NOT NULL,    -- 评论内容
                             create_time TIMESTAMP NOT NULL -- 创建时间
);

-- Forum table
CREATE TABLE bbs_forum (
                           id          INTEGER PRIMARY KEY AUTOINCREMENT,
                           title       TEXT NOT NULL,     -- 标题
                           cover_image TEXT NOT NULL,     -- 封面图
                           user_id     INTEGER NOT NULL,  -- 发布用户的id
                           content     TEXT,              -- 正文内容
                           delete_flag INTEGER NOT NULL,  -- 删除标记，0正常，1删除
                           create_time TIMESTAMP NOT NULL, -- 创建时间
                           update_time TIMESTAMP NOT NULL  -- 修改时间
);

-- User table
CREATE TABLE bbs_user (
                          id          INTEGER PRIMARY KEY AUTOINCREMENT,
                          account     TEXT NOT NULL,     -- 账号
                          name        TEXT NOT NULL,     -- 名称
                          password    TEXT NOT NULL,     -- 密码
                          mail        TEXT NOT NULL,     -- 邮箱
                          head        TEXT NOT NULL,     -- 头像文件名称
                          auth        INTEGER NOT NULL,  -- 权限登记，0用户，1管理员
                          create_time TIMESTAMP NOT NULL, -- 创建时间
                          update_time TIMESTAMP NOT NULL  -- 修改时间
);