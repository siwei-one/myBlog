package com.yuer.dao;

import java.util.List;

import com.yuer.entity.Comment;

public interface ICommentDao {

	// 增加评论
	Integer saveComment(Comment comment);
	
	// 根据博客id列出评论
	List<Comment> listCommentByBlogId(Long blogId);
	
	// 获取父节点为该id的子节点
	List<Comment> getComments(Long commentId);
	
	// 根据博客id和是不是根评论列出评论
	List<Comment> listCommentByBlogIdAndRoot(Long blogId);
	
	Comment getCommentById(Long id);

	
	

}
