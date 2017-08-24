package com.citroen.ledp.vo;

import com.citroen.ledp.dao.mybaits.Column;

import java.util.Date;

/**
 * Created by maskx on 2017/5/24.
 */
public class DealerMappingVO {

    private Long id;

    @Column(name = "source_dealer")
    private String sourceDealer;

    @Column(name = "source_dealer_name")
    private String sourceDealerName;

    @Column(name = "target_dealer")
    private String targetDealer;

    @Column(name = "target_dealer_name")
    private String targetDealerName;

    @Column(name = "mapping_reason_phrase")
    private String mappingReasonPhrase;

    @Column(name = "mapping_beg_date")
    private String mappingBegDate;

    @Column(name = "mapping_end_date")
    private String mappingEndDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceDealer() {
        return sourceDealer;
    }

    public void setSourceDealer(String sourceDealer) {
        this.sourceDealer = sourceDealer;
    }

    public String getSourceDealerName() {
        return sourceDealerName;
    }

    public void setSourceDealerName(String sourceDealerName) {
        this.sourceDealerName = sourceDealerName;
    }

    public String getTargetDealer() {
        return targetDealer;
    }

    public void setTargetDealer(String targetDealer) {
        this.targetDealer = targetDealer;
    }

    public String getTargetDealerName() {
        return targetDealerName;
    }

    public void setTargetDealerName(String targetDealerName) {
        this.targetDealerName = targetDealerName;
    }

    public String getMappingReasonPhrase() {
        return mappingReasonPhrase;
    }

    public void setMappingReasonPhrase(String mappingReasonPhrase) {
        this.mappingReasonPhrase = mappingReasonPhrase;
    }

	public String getMappingBegDate() {
		return mappingBegDate;
	}

	public void setMappingBegDate(String mappingBegDate) {
		this.mappingBegDate = mappingBegDate;
	}

	public String getMappingEndDate() {
		return mappingEndDate;
	}

	public void setMappingEndDate(String mappingEndDate) {
		this.mappingEndDate = mappingEndDate;
	}
}
