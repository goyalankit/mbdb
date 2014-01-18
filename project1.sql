-- query 1: what is the price of the part named "Dirty Harry"?
select price from parts where pname='Dirty Harry';

-- query 2: What orders have been shipped after date '03-feb-95'?
select * from orders where TO_DATE(SHIPPED) >TO_DATE('03-feb-95');

--CHECK THIS ONE
-- query 3: What are the ono and cname values of customers whose orders have not been shipped (i.e., the shipped column has a null value)?
select ORDERS.ONO, CUSTOMERS.CNAME from CUSTOMERS, ORDERS where ORDERS.SHIPPED is NULL and ORDERS.CNO = CUSTOMERS.CNO;

-- query 4: Retrieve the names of parts whose quantity on hand (QOH) is between 20 and 70.
select PNAME from PARTS where QOH > 20 and QOH < 70;

--CHECK THIS ONE
-- query 5: Get all unique pairs of cno values for customers that have the same zip code.
select c1.cno, c2.cno from customers c1 LEFT JOIN customers c2 on c1.zip = c2.zip where c1.cno < c2.cno;

-- query 6: Create a nested SQL select statement that returns the cname values of customers who have placed orders with employees living in Fort Dodge.
SELECT c1.cname from CUSTOMERS c1 join orders o1 on c1.cno=o1.cno where o1.eno in (select eno from employees where zip in ( select zip from zipcodes where city='Fort Dodge')); 

-- query 7: What orders have been shipped to Wichita?
select * from orders o1 where o1.cno in (select c1.cno from customers c1 where c1.zip in (select zip from zipcodes where city='Wichita'));

-- query 8: Get the pname values of parts with the lowest price.
select pname from parts where price in (select min(price) from parts);

-- query 9: What is the name of the part with the lowest price? (use qualified comparison in your predicate, i.e., <=all).
select pname from parts where price <= all(select price from parts);

-- query 10: What parts cost more than the most expensive Land Before Time part? (Hint: you should use pattern-matching, e.g., pname like 'Land Before Time%').
select * from parts where pname like 'Land Before Time%';
