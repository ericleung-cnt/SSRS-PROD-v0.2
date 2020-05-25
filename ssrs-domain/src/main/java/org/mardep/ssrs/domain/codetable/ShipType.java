package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Entity
@Table(name="SHIP_TYPES")
public class ShipType extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	public static String CARGO_SHIP = "CGO";
	public static String PASSENGER_SHIP = "PAX";
	public static String TANKER = "TAN";
	public static String TUG = "TUG";
	public static String YACHT = "YHT";


	@Id
	@Setter
	@Column(name = "SHIP_TYPE_CODE", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "ST_DESC", nullable=false, length=30)
	private String stDesc;

	@Override
	public String getId() {
		return id;
	}

}
