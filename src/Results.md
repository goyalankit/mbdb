#### Results from different queries.

**Should join the three tables**

```
   select * from zipcodes, customers, employees where zipcodes.zip=customers.zip and employees.zip=zipcodes.zip;
   .

   employees.eno,employees.ename,employees.zip,employees.hstr,zipcodes.zip,zipcodes.city,customers.cno,customers.cname,customers.street,customers.zip,customers.phone

   1000,"Jones",67226,"12-DEC-95",67226,"Wichita",2222,"Bertram","237 Ash Avenue",67226,"316-689-5555"

   1001,"Smith",60606,"01-JAN-92",60606,"Fort Dodge",3333,"Barbara","111 Inwood St.",60606,"316-111-1234"

   1000,"Jones",67226,"12-DEC-95",67226,"Wichita",1111,"Charles","123 Main St.",67226,"316-636-5555"
```

**Should join the two tables**

```
   select * from zipcodes, customers where zipcodes.zip=customers.zip;
   .

   zipcodes.zip,zipcodes.city,customers.cno,customers.cname,customers.street,customers.zip,customers.phone

   60606,"Fort Dodge",3333,"Barbara","111 Inwood St.",60606,"316-111-1234"

   67226,"Wichita",2222,"Bertram","237 Ash Avenue",67226,"316-689-5555"

   67226,"Wichita",1111,"Charles","123 Main St.",67226,"316-636-5555"


```

```
select * from employees;
.

1000,"Jones",67226,"12-DEC-95"

1001,"Smith",60606,"01-JAN-92"

1002,"Brown",50302,"01-SEP-94"


```

**should return the cross product of join of zipcodes and customers with employess** `3*3 = 9 records`
```
select * from zipcodes, customers, employees where zipcodes.zip=customers.zip;

zipcodes.zip,zipcodes.city,customers.cno,customers.cname,customers.street,customers.zip,customers.phone,employees.eno,employees.ename,employees.zip,employees.hstr

67226,"Wichita",1111,"Charles","123 Main St.",67226,"316-636-5555",1001,"Smith",60606,"01-JAN-92"

67226,"Wichita",2222,"Bertram","237 Ash Avenue",67226,"316-689-5555",1000,"Jones",67226,"12-DEC-95"

60606,"Fort Dodge",3333,"Barbara","111 Inwood St.",60606,"316-111-1234",1000,"Jones",67226,"12-DEC-95"

67226,"Wichita",1111,"Charles","123 Main St.",67226,"316-636-5555",1002,"Brown",50302,"01-SEP-94"

67226,"Wichita",2222,"Bertram","237 Ash Avenue",67226,"316-689-5555",1001,"Smith",60606,"01-JAN-92"

60606,"Fort Dodge",3333,"Barbara","111 Inwood St.",60606,"316-111-1234",1002,"Brown",50302,"01-SEP-94"

60606,"Fort Dodge",3333,"Barbara","111 Inwood St.",60606,"316-111-1234",1001,"Smith",60606,"01-JAN-92"

67226,"Wichita",2222,"Bertram","237 Ash Avenue",67226,"316-689-5555",1002,"Brown",50302,"01-SEP-94"

67226,"Wichita",1111,"Charles","123 Main St.",67226,"316-636-5555",1000,"Jones",67226,"12-DEC-95"

```

```
select * from zipcodes where zipcodes.zip = zipcodes.zip;
.

zipcodes.zip,zipcodes.city,zipcodes.zip,zipcodes.city

61111,"Fort Hays",61111,"Fort Hays"

60606,"Fort Dodge",60606,"Fort Dodge"

50302,"Kansas City",50302,"Kansas City"

54444,"Columbia",54444,"Columbia"

67226,"Wichita",67226,"Wichita"

66002,"Liberal",66002,"Liberal"
```

```
```