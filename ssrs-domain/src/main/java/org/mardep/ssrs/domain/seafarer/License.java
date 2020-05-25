package org.mardep.ssrs.domain.seafarer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="SEAFARER_LICENSE")
@IdClass(CommonPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"seafarerId", "seqNo"})
public class License extends AbstractPersistentEntity<CommonPK> {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public final static SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
	@Transient
	public final static String DATE_TYPE_ON 	= "on";
	@Transient
	public final static String DATE_TYPE_FROM 	= "from";
	
	@Id
	@Getter
	@Setter
	@Column(name = "COURSE_NO", nullable=false)
	private Integer seqNo;

	@Id
	@Getter
	@Setter
	@Column(name = "SEAFARER_ID", nullable=false, length=20)
	private String seafarerId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFL_FK"))
	private Seafarer seafarer;

	@Setter
	@Column(name = "COURSE_DESC", length=400)
	private String courseDesc;
	
	public String getCourseDesc() {
		try{
//			String date = "14/FEB/2019";
			if(StringUtils.isNotBlank(courseDesc)){
				if(courseDesc.indexOf(" on ")>=0){
					String[] onDateArray = courseDesc.split(" on");
					String descTemp = onDateArray[0].trim();
					String onDateStr = onDateArray[1].trim();
					
					setFrom(df.parse(onDateStr));
					setDateType(DATE_TYPE_ON);
					setCourseDescTemp(descTemp);
				}else if(courseDesc.indexOf(" from")>=0 && courseDesc.indexOf("to")>=0){
					String[] fromStrArray = courseDesc.split("from");
					String descTemp = fromStrArray[0].trim();
					String[] fromToDateStr = fromStrArray[1].split("to");
					String fromDateStr = fromToDateStr[0].trim();
					String 	 toDateStr = fromToDateStr[1].trim();

					setFrom(df.parse(fromDateStr));
					setTo(df.parse(toDateStr));
					setDateType(DATE_TYPE_FROM);
					setCourseDescTemp(descTemp);
				}else{
					setCourseDescTemp(courseDesc);
				}
			}
		}catch(Exception ex){
			logger.warn("Fail to parse date from:{}", courseDesc);
			setDateType(null);
			setFrom(null);
			setTo(null);
			setCourseDescTemp(courseDesc);
		}
		return courseDesc;
	}
	
	@Getter
	@Setter
	@Transient
	private String courseDescTemp; 

	@Getter
	@Setter
	@Transient
	private String dateType; 
	
	@Getter
	@Setter
	@Transient
	private Date from;

	@Getter
	@Setter
	@Transient
	private Date to;
	
	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}

}
