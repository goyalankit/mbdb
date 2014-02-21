open "mydbenv";
.
select * from zipcodes, customers, orders where zipcodes.zip = customers.zip and orders.cno = customers.cno;
.