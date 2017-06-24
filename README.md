# <font color="#000fff" size=9 >代码生成工具思路</font>

## 1. **利用自己framework框架生成项目分层结构 [contract,biz,core,dal,job,service,web,solr]**

| layer | description |
| ----- | ----------- |
| contract | api接口层 |
| biz      | 业务层    |
| core     | 核心层    |
| dal      | 数据库访问层 |
| job      | job调度层   |
| service  | 服务层      |
| web      | restful层  |
| solr     | 搜索引擎    |
## 2. **利用mybatis-generator工具生成基本的mybatis框架三层结构 [javaModelGenerator javaClientGenerator sqlMapGenerator]**
 - javaModelGenerator
 - javaClientGenerator
 - sqlMapGenerator
## 3. **根据数据库表结构生成分层的对应代码[Mapper.xml dao model]**

 - dal层，目录7个，分别为src中包含dal,model,model.ext,dao,dao.ext, resource中为 mapper,mapper.ext
dao生成的代码包含如下：
基本方法 以表Coupon为例
```java
    int countByExample(CouponExample example);

    int deleteByExample(CouponExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Coupon record);

    int insertSelective(Coupon record);

    List<Coupon> selectByExample(CouponExample example);

    Coupon selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Coupon record, @Param("example") CouponExample example);

    int updateByExample(@Param("record") Coupon record, @Param("example") CouponExample example);

    int updateByPrimaryKeySelective(Coupon record);

    int updateByPrimaryKey(Coupon record);
```
 - 额外生成的方法：
```java
	int logicalDeleteByPrimaryKey(
        		@Param("id")Integer id,
        		@Param("updateUser")String updateUser,
				@Param("updateUserName")String updateUserName,
				@Param("updateTime")Date updateTime);

    Coupon selectByPrimaryKeyForUpdate(Integer id);

    List<Coupon> queryCoupon(CouponQueryReq couponQuery);

	int changeStatus(@Param("id")Integer id, byte status, byte newStatus);
```
 - 根据索引生成的get方法：
```java
	List<Coupon> selectByCouponNo(@Param("couponNo") String couponNo);
```
## 对应Mapper.xml中SQL语句和返回对象

### contract层：
constant,enum,dto,service,req,resp
#### 代码主要生成为4部分 1.enum  2.service 3.req 4.resp


## biz层生成的代码如下：
biz,cache,convertor,support,util
#### 代码主要生成为2部分 1.biz  2.convertor

## service层生成的代码如下：
intercepter,validate,service.impl,util,junit.test
#### 代码主要生成为3部分 1.validate  2.service.impl 3.junit.test

## web层生成的代码如下：
config,controller,util,vo,convertor,util,intercepter,domain
#### 代码主要生成为3部分：1.controller  2.vo 3.convertor

