create table app_user (
    id bigserial primary key,
    username varchar(50) not null unique,
    display_name varchar(80) not null,
    password_hash varchar(255) not null,
    demo_account boolean not null default false,
    created_at timestamptz not null default current_timestamp
);

create table route_watch (
    id bigserial primary key,
    user_id bigint not null references app_user (id) on delete cascade,
    name varchar(80) not null,
    road_ids varchar(255) not null,
    notes varchar(255),
    demo_data boolean not null default false,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table incident_cache_entry (
    id bigserial primary key,
    road_id varchar(30) not null,
    category varchar(30) not null,
    payload_json text not null,
    cached_at timestamptz not null default current_timestamp,
    constraint uq_incident_cache unique (road_id, category)
);

create index idx_route_watch_user_id on route_watch (user_id);
create index idx_incident_cache_road_id on incident_cache_entry (road_id);
