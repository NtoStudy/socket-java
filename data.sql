-- auto-generated definition
create table chat_rooms
(
    room_id           int unsigned auto_increment comment '聊天室唯一标识'
        primary key,
    room_name         varchar(100)                        not null comment '聊天室名称',
    creator_id        int unsigned                        not null comment '创建者ID',
    created_at        timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    avatar_url        text                                null comment '聊天室头像',
    group_number      varchar(10)                         null comment '群组编号',
    announcement      text                                null comment '群公告',
    pinned_message_id int unsigned                        null comment '置顶消息ID'
)
    comment '聊天室表';

-- auto-generated definition
create table friend_groups
(
    group_id     int unsigned auto_increment comment '分组ID'
        primary key,
    user_id      int unsigned  not null comment '用户ID',
    group_name   varchar(50)   not null comment '分组名称',
    sort_order   int           null comment '自定义排序序号',
    member_count int default 0 null comment '分组中的人数',
    constraint fk_user_id
        foreign key (user_id) references users (user_id)
)
    comment '好友分组表';

-- auto-generated definition
create table friends
(
    relation_id int unsigned auto_increment comment '好友关系唯一标识'
        primary key,
    user_id     int unsigned                         not null comment '用户ID',
    friend_id   int unsigned                         not null comment '好友ID',
    status      tinyint    default 0                 null comment '好友关系状态',
    created_at  timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    group_id    int unsigned                         null comment '分组ID',
    remark      varchar(100)                         null comment '好友备注',
    is_pinned   tinyint(1) default 0                 null comment '是否置顶好友',
    is_blocked  tinyint(1) default 0                 null comment '是否拉黑'
)
    comment '好友关系表';

-- auto-generated definition
create table group_messages
(
    message_id          int unsigned auto_increment comment '消息唯一标识'
        primary key,
    sender_id           int unsigned                          not null comment '发送者ID',
    chat_room_id        int unsigned                          not null comment '聊天室ID',
    content             text                                  not null comment '消息内容',
    sent_time           timestamp   default CURRENT_TIMESTAMP null comment '发送时间',
    is_read             tinyint     default 0                 null comment '消息是否已读',
    deleted_by_admin    tinyint     default 0                 null comment '管理员是否删除该消息',
    deleted_by_users    json                                  null comment '删除该消息的用户ID列表',
    message_type        varchar(50) default 'text'            not null comment '消息类型',
    mentioned_user_ids  json                                  null comment '被@的用户ID列表',
    reply_to_message_id int unsigned                          null comment '回复的消息ID'
)
    comment '群聊消息表';

-- auto-generated definition
create table message_favorites
(
    favorite_id int unsigned auto_increment comment '收藏ID'
        primary key,
    user_id     int unsigned                        not null comment '用户ID',
    message_id  int unsigned                        not null comment '消息ID',
    created_at  timestamp default CURRENT_TIMESTAMP not null comment '收藏时间',
    constraint fk_message_favorites_message_id
        foreign key (message_id) references messages (message_id),
    constraint fk_message_favorites_user_id
        foreign key (user_id) references users (user_id)
)
    comment '消息收藏表';


-- auto-generated definition
create table messages
(
    message_id          int unsigned auto_increment comment '消息唯一标识'
        primary key,
    sender_id           int unsigned                          not null comment '发送者ID',
    receiver_id         int unsigned                          not null comment '接收者ID',
    content             text                                  not null comment '消息内容',
    sent_time           timestamp   default CURRENT_TIMESTAMP null comment '发送时间',
    is_read             tinyint     default 0                 null comment '消息是否已读',
    deleted_by_sender   tinyint     default 0                 null comment '发送者是否删除该消息',
    deleted_by_receiver tinyint     default 0                 null comment '接收者是否删除该消息',
    message_type        varchar(50) default 'text'            not null comment '消息类型',
    is_recalled         tinyint(1)  default 0                 null comment '是否撤回',
    recalled_at         timestamp                             null comment '撤回时间',
    reply_to_message_id int unsigned                          null comment '回复的消息ID'
)
    comment '聊天消息表';

-- auto-generated definition
create table notifications
(
    notification_id int unsigned auto_increment comment '通知唯一标识'
        primary key,
    receiver_id     int unsigned                         not null comment '接收者ID',
    content         text                                 not null comment '通知内容',
    type            varchar(50)                          not null comment '通知类型',
    status          tinyint    default 0                 null comment '通知状态',
    created_at      timestamp  default CURRENT_TIMESTAMP null comment '通知创建时间',
    related_id      int unsigned                         null comment '关联的记录ID',
    creator_id      int unsigned                         null comment '关联的创建者ID（用于群聊邀请等）',
    is_announcement tinyint(1) default 0                 null comment '是否为系统公告',
    expires_at      timestamp                            null comment '公告过期时间'
)
    comment '系统通知表';

-- auto-generated definition
create table post_comments
(
    comment_id        int auto_increment comment '评论ID'
        primary key,
    post_id           int                                 not null comment '动态ID',
    user_id           int unsigned                        not null comment '用户ID',
    content           text                                not null comment '评论内容',
    created_at        timestamp default CURRENT_TIMESTAMP not null comment '评论时间',
    parent_comment_id int                                 null comment '父评论ID',
    constraint fk_post_comments_post_id
        foreign key (post_id) references user_posts (post_id),
    constraint fk_post_comments_user_id
        foreign key (user_id) references users (user_id)
)
    comment '动态评论表';

-- auto-generated definition
create table post_likes
(
    like_id    int auto_increment comment '点赞ID'
        primary key,
    post_id    int                                 not null comment '动态ID',
    user_id    int unsigned                        not null comment '用户ID',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '点赞时间',
    constraint fk_post_likes_post_id
        foreign key (post_id) references user_posts (post_id),
    constraint fk_post_likes_user_id
        foreign key (user_id) references users (user_id)
)
    comment '动态点赞表';

-- auto-generated definition
create table post_media
(
    media_id   int auto_increment comment '媒体ID'
        primary key,
    post_id    int                     not null comment '动态ID',
    media_type enum ('image', 'video') not null comment '媒体类型',
    media_url  varchar(1000)           not null comment '媒体URL',
    constraint fk_post_media_post_id
        foreign key (post_id) references user_posts (post_id)
)
    comment '动态媒体表';

-- auto-generated definition
create table user_chat_rooms
(
    user_chat_room_id int unsigned auto_increment comment '用户-聊天室关系唯一标识'
        primary key,
    user_id           int unsigned                         not null comment '用户ID',
    room_id           int unsigned                         not null comment '聊天室ID',
    joined_at         timestamp  default CURRENT_TIMESTAMP null comment '加入时间',
    status            tinyint    default 0                 null comment '用户在聊天室中的状态',
    role              enum ('群主', '管理员', '普通成员')  null comment '角色类型',
    is_pinned         tinyint(1) default 0                 null comment '是否置顶',
    muted_until       timestamp                            null comment '禁言截止时间'
)
    comment '用户-聊天室关联表';

-- auto-generated definition
create table user_posts
(
    post_id         int auto_increment comment '动态ID'
        primary key,
    user_id         int unsigned                                                    not null comment '用户ID',
    content         longtext                                                        null comment '动态内容',
    like_count      int                                   default 0                 not null comment '点赞数',
    comment_count   int                                   default 0                 not null comment '评论数',
    tags            text                                                            null comment '标签',
    created_at      timestamp                             default CURRENT_TIMESTAMP not null comment '发布时间',
    privacy_setting enum ('public', 'friends', 'private') default 'public'          not null comment '隐私设置',
    is_deleted      tinyint(1)                            default 0                 not null comment '是否删除',
    constraint fk_user_posts_user_id
        foreign key (user_id) references users (user_id)
)
    comment '用户动态表';

-- auto-generated definition
create table users
(
    user_id       int unsigned auto_increment comment '用户唯一标识'
        primary key,
    username      varchar(50)                                                  not null comment '用户名',
    number        varchar(10)                                                  not null comment '随机分配的十位数字',
    password      varchar(255)                                                 not null comment '用户密码',
    avatar_url    text                                                         null comment '用户头像URL',
    created_at    timestamp                          default CURRENT_TIMESTAMP null comment '注册时间',
    status        varchar(50)                                                  null comment '用户状态',
    custom_status varchar(255)                                                 null comment '用户自定义状态',
    birthday      date                                                         null comment '生日',
    hobbies       text                                                         null comment '兴趣爱好',
    signature     varchar(255)                                                 null comment '个性签名',
    gender        enum ('male', 'female', 'unknown') default 'unknown'         null comment '性别',
    province      varchar(50)                                                  null comment '省',
    city          varchar(50)                                                  null comment '市',
    like_count    int                                default 0                 null comment '被点赞的个数',
    constraint number
        unique (number)
)
    comment '用户表';

