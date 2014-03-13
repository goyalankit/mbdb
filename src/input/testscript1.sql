select * from products
where price >= 20 and price <= 30;
.
select cname, cloc
from customers
where cloc="austin";
.
select cname
from customers 
where cid=1000;
.
update products
set price=31
where price=30;
.
update customers 
set cname="abc"
where cname="xyz";
.
delete products
where pid=150;
.
delete orders
where c_id=50;
.
select cname,pname,qty
from customers,products,orders
where c_id=cid and p_id=pid and cname="blad" and qty < 10;
.
select pname
from products,orders
where pid=p_id and qty>15;
.
select cname
from customers,orders
where cid = c_id and cloc = "austin" and qty>=5 and qty <= 10;
.

