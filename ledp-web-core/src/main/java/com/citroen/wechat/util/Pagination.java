package com.citroen.wechat.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author 何海粟
 * @date2015年6月11日
 */
public class Pagination implements Serializable{
	private int currentPage;
	private int total;
	private int pageNumber;
	private Set<Integer> pages = new TreeSet<Integer>();
	
	public Pagination(){}
	
	public Pagination(int count, int pageNumber , int currentPage){
		this.currentPage = currentPage;
		this.pageNumber = pageNumber;
		init(count);
	}
	private void init(int count){
		total = 0;
		if(count%pageNumber==0){
			total = count/pageNumber;
		}else{
			total = count/pageNumber+1;
		}
		if(total == 0){
			pages.add(1);
		}
		if(total<=5 || currentPage<3){
			for(int i=1; i<=total; i++){
				if(i==6){
					break;
				}
				pages.add(i);
			}
			
		}else{
			//尾页
			if(total == currentPage){
				for(int i=-4; i<1; i++){
					pages.add(currentPage+i);
				}
			}
			if(total - currentPage == 1){
				for(int i=-3; i<2; i++){
					pages.add(currentPage+i);
				}
			}
			for(int i=-2; i<3; i++){
				if((currentPage+i)<=total){
					pages.add(currentPage+i);
				}
			}
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Set<Integer> getPages() {
		return pages;
	}

	public void setPages(Set<Integer> pages) {
		this.pages = pages;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
}