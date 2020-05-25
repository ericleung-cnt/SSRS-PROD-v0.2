/***************************************************************************************************************
* Qualified Name	: 	org.mardep.fsqc.pojo.FsqcShipResultData.java
* Created By		: 	Albert Chan
* Created date		: 	2019-08-16
* ************************************************************************************************************** 
* Change log:
* log no	Change complete date	Developer			Change Reason
* ======	====================	=========			=============
* 00000		2019-08-16				Albert Chan			Initial Implementation
*
* 
****************************************************************************************************************/
package org.mardep.ssrs.fsqcwebService.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class FsqcShipErrorData{

	@Getter
	@Setter
	String field;

	@Getter
	@Setter
	String message;

}
