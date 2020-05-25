package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="SHIP_SUBTYPES")
@IdClass(ShipSubTypePK.class)
@ToString(of={"shipTypeCode", "shipSubTypeCode"})
public class ShipSubType extends AbstractPersistentEntity<ShipSubTypePK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Getter
	@Column(name = "ST_SHIP_TYPE_CODE", nullable=false, length=50)
	private String shipTypeCode;

	@Id
	@Setter
	@Getter
	@Column(name = "SHIP_SUBTYPE_CODE", nullable=false, length=50)
	private String shipSubTypeCode;

	@Getter
	@Setter
	@Column(name = "SS_DESC", nullable=false, length=30)
	private String ssDesc;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ST_SHIP_TYPE_CODE", referencedColumnName="SHIP_TYPE_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SS_ST_FK"))
	private ShipType shipType;


	@Override
	public ShipSubTypePK getId() {
		return new ShipSubTypePK(shipTypeCode, shipSubTypeCode);
	}

}
