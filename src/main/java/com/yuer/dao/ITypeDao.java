package com.yuer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yuer.entity.Type;

public interface ITypeDao {

	// 增加
	Integer saveType(Type type);

	// 删除
	void deleteType(Long id);

	// 改
	Integer updateType(@Param("id") Long id, @Param("typeName") String typeName);

	// 查
	
	// 这里根据一个参数查优化一下，
	Type getTypeByColumn(@Param("column") String column, @Param("value") String value);
	
	// 全查出来
	List<Type> listType();

	// 用来做首页的根据分类下的博客数量最高的几个分类
	List<Type> listTypeTop(Integer size);

	// 根据start和size查询
	List<Type> listTypeByParam(Integer start, Integer size);

	// 根据typeId查询出该type下的blog数量
	Integer listBlogNum(Long id);

	// 获取总数据量
	Integer getTotal();

}
