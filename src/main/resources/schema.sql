CREATE VIEW vw_coa AS 
select a.coa_no AS coa_no,a.coa_desc AS coa_desc,'ASSET' AS coa_type 
	from (values row('1001','Bank'),row('1002','Accounts receivable')
) a (coa_no,coa_desc) 
where true 
union 
select a.coa_no AS coa_no,a.coa_desc AS coa_desc,'LIABILITY' AS coa_type 
	from (values row('2001','Customer funds'),row('2002','Accounts payable')
) a (coa_no,coa_desc) 
where true 
union 
select a.coa_no AS coa_no,a.coa_desc AS coa_desc,'REVENUE' AS coa_type 
	from (values row('3001','Premium')) a (coa_no,coa_desc) 
where true 
union 
select a.coa_no AS coa_no,a.coa_desc AS coa_desc,'EQUITY' AS coa_type 
	from (values row('4001','Drawings')
) a (coa_no,coa_desc) 
where true;