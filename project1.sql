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
select * from parts where price > all(select max(price) from parts where pname in (select pname from parts where pname like 'Land Before Time%'));

-- query 11: Write a correlated query to return the cities of zipcodes from which an order has been placed.
select distinct(z1.city) from zipcodes z1 join customers c1 on z1.zip=c1.zip where c1.CNO in (select cno from orders o1 where o1.cno=c1.cno);

-- query 12: Get cname values of customers who have placed at least one part order through employee with eno = 1000.
select c1.cname from customers c1 where c1.cno in (select cno from orders where orders.ENO=1000);

-- query 13: Get the total number of customers.
select count(*) from customers;

-- query 14: Get the pname values of parts that cost more than the average cost of all parts.
select p1.pname from parts p1 where p1.price > all(select avg(price) from parts );

-- query 15: For each part, get pno and pname values along with the total sales in dollars.--select ono, sum(qty) from odetails group by ono;
select od1.pno, p1.pname, sum(od1.qty * p1.price) "total sales in $" from odetails od1 join parts p1 on od1.pno=p1.pno group by od1.pno, p1.pname;

-- query 16: For each part, get pno and pname values along with the total sales in dollars, but only for total sales exceeding $200.
select * from (select od1.pno, p1.pname, sum(od1.qty * p1.price) sales_total from odetails od1 join parts p1 on od1.pno=p1.pno group by od1.pno, p1.pname) where sales_total > 200;

-- query 17: Repeat the last 2 queries, except this time create a view to simplify your work. Define the view and each query on that view.
create view sales as select od1.pno, p1.pname, sum(od1.qty * p1.price) total_sales from odetails od1 join parts p1 on od1.pno=p1.pno group by od1.pno, p1.pname;
select * from sales;
select * from sales where total_sales > 200;

-- query 18: Delete order 1021 and its order details.
delete from odetails where ono=1021;
delete from orders where ono=1021;

-- query 19: Increase the cost of all parts by 5%.
select * from parts;
update parts set price=(price + 0.05 * price);

-- query 20: Retrieve employees by name in reverse alphabetical order.
select ename from employees order by ename desc;

-- query 21: What tuples of Employees and Zipcodes do not participate in a join of these relations? Use the outerjoin and minus operations.
-- To get the tuples of Zipcodes that doesn't participate in a join of these relations
(select z1.* from employees e1 right outer join zipcodes z1 on e1.zip=z1.zip) minus (select z1.* from employees e1 join zipcodes z1 on e1.zip=z1.zip);

-- To get the tuples of Employees that doesn't participate in a join of these relations
(select e1.* from employees e1 left outer join zipcodes z1 on e1.zip=z1.zip) minus (select e1.* from employees e1 join zipcodes z1 on e1.zip=z1.zip);

-- Single Query
((select * from employees e1 right outer join zipcodes z1 on e1.zip=z1.zip) union (select * from employees e1 left outer join zipcodes z1 on e1.zip=z1.zip)) minus (select * from employees e1 join zipcodes z1 on e1.zip=z1.zip);
