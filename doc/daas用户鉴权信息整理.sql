-- 1.关联得出全对应关系
drop table if exists dig.daas_user_key_resource;
create table dig.daas_user_resource (
username varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '用户名',
accesskey varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment 'accesskey',
secretkey varchar(100) character set utf8mb4 collate utf8mb4_general_ci default null comment 'secretkey',
tenantname varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '项目名',
resourcename varchar(100) character set utf8mb4 collate utf8mb4_general_ci default null comment '资源名',
resourceid varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '资源id',
resourcecode varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '资源代码'
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

insert into dig.daas_user_key_resource 
select distinct 
	a.name,
	a.access_key,
	a.secret_key,
	c.tenant_name,
	f.resource_name,
	f.resource_id,
	f.resource_code
from dig.sec_user a -- daas用户表
inner join dig.sec_user_tenant b on a.id = b.user_id -- daas用户表对应项目id
inner join dig.metadata_tenant c on b.tenant_id = c.tenant_id -- daas项目表
inner join daas_datalake.data_resource_approval d on c.tenant_id = d.request_tenant_id and d.status = 'approval' -- daas项目表对应审批id,只要被审批的
inner join daas_datalake.data_resource_ref_approval e on e.approval_id = d.approval_id -- daas审批表对应资源表
inner join daas_datalake.data_resource f on f.resource_id = e.resource_id and f.status in ('pending','published','updating') -- 只要可用的资源
where a.status = 'enabled' 
order by a.name;

-- 2.分表
drop table if exists dig.daas_user_key;
create table dig.daas_user_key (
  username varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '用户名',
  accesskey varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment 'accesskey',
  secretkey varchar(100) character set utf8mb4 collate utf8mb4_general_ci default null comment 'secretkey'
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

insert into dig.daas_user_key 
select distinct 
	name,
	access_key,
	secret_key 
from dig.sec_user where status = 'enabled';

drop table if exists dig.daas_user_resource;
create table dig.daas_user_resource (
username varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '用户名',
tenantname varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '项目名',
resourcename varchar(100) character set utf8mb4 collate utf8mb4_general_ci default null comment '资源名',
resourceid varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '资源id',
resourcecode varchar(50) character set utf8mb4 collate utf8mb4_general_ci default null comment '资源代码'
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

insert into dig.daas_user_resource 
select distinct 
	username,
	tenantname,
	resourcename,
	resourceid,
	resourcecode 
from dig.daas_user_key_resource;