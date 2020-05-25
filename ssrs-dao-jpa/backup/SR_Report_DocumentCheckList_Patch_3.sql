UPDATE DOCUMENT_CHECKLIST  SET DOCUMENT_TYPE='ACD_CSR' WHERE DOCUMENT_TYPE='CSR' and CHECKLIST_DESC_CHI IS NOT NULL
AND CHECKLIST_DESC in (
'An original Continuous Synopsis Record (CSR) No.${CSR}',
'A duplicate Continuous Synopsis Record (CSR) No.${CSR}',
'A Demand Note no. ${DN}.'
)