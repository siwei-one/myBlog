package com.yuer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yuer.entity.Blog;
import com.yuer.entity.BlogQuery;

public interface IBlogDao {

	// 增加（由于增加博客涉及一系列操作，所以统一和增加博客相关的标签表操作也放在着这）
	Integer saveBlog(Blog blog);

	Integer saveTags(Long blogId, Long tagId);

	Integer deleteTags(Long blogId);

	List<Long> getTags(Long blogId);

	// 删除
	Integer deleteBlog(Long id);

	// 改
	Integer updateBlog(Blog blog);

	// 查

	// 这里根据一个参数查优化一下，
	Blog getBlogByColumn(@Param("column") String column, @Param("value") String value);

	// 全查出来
	List<Blog> listBlog();

	// 查top的几个，size传过来
	List<Blog> listBlogTop(Integer size);

	// 根据start和size查询(分页后台的，无论是否发布都要显示)
	List<Blog> listBlogByParam(Integer start, Integer size);

	// 前台的(分页不包含草稿状态的)
	List<Blog> listBlogByParamAndPublished(Integer start, Integer size);

	// 组合查询后再分页
	public List<Blog> getBlogByParams(@Param("blog") BlogQuery blog, @Param("start") Integer start,
			@Param("size") Integer size);

	// 根据组合查询出的总数
	Integer getTotalByParams(BlogQuery blog);

	// 根据搜索结果再分页
	List<Blog> listBlogBySerach(@Param("query") String query, @Param("start") Integer start,
			@Param("size") Integer size);

	// 根据类别分页
	List<Blog> listBlogByTypeId(Long id, Integer start, Integer size);

	// 根据标签分页
	List<Blog> listBlogByTagId(Long id, Integer start, Integer size);
	
	// 获取总数据量
	Integer getTotal();

	// 获取发布的总数据量
	Integer getTotalAndPublished();

	// 下面几个都是不同专栏下的总数据量
	Integer getTotalAndPublishedAndSearch(String query);

	Integer getTotalAndPublishedAndTypeId(Long id);

	Integer getTotalAndPublishedAndTagId(Long id);

	
	// 更新游览次数
	Integer updateViewCounts(Long id);

	// 以年分组查询出来年份
	List<String> findGroupYear();

	// 根据年份查出(这里就不整合到根据一个参数查询的了)
	List<Blog> findByYear(String year);
}
