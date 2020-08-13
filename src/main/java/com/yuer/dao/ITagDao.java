package com.yuer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yuer.entity.Tag;
import com.yuer.entity.Type;

public interface ITagDao {

	// 增加
	Integer saveTag(Tag tag);

	// 删除
	void deleteTag(Long id);

	// 改
	Integer updateTag(@Param("id") Long id, @Param("tagName") String tagName);

	// 查
	
	// 这里根据一个参数查优化一下，根据id,tag_name直接传即可
	Tag getTagByColumn(@Param("column") String column, @Param("value") String value);

	// 全查出来
	List<Tag> listTag();

	// 用来做首页的根据分类下的博客数量最高的几个分类
	List<Tag> listTagTop(Integer size);
	
	// 根据typeId查询出该tag下的blog数量
	Integer listBlogNum(Long id);

	// 根据start和size查询
	List<Tag> listTagByParam(Integer start, Integer size);
	
	// 根据博客id查出所属得标签
	List<Tag> listTagByBlogId(Long id);
	
	
	// 获取总数据量
	Integer getTotal();
	
	
	
	

}
