### Notes

#### Completed
1. Conjunctive single relation select query is functional.

#### TODO:
1. Add support for multiple relations. `Join` relations.
2. Implement delete and update commands
3. Verify if we need support for Disjunctive queries.
4. Read on index creation and stuff.

### Test cases:
1. Two relations mentioned but the condition is not given for the join.

`select * from zipcodes, customers  where zipcodes.zip=54444;`

2. zip here is ambiguious

`select * from zipcodes, customers  where zip=54444;`






####Results
select * from zipcodes, customers, orders where zipcodes.zip = customers.zip and orders.cno = customers.cno;
1023,3333,1000,"20-JUN-97","30-SEP-96",60606,"Fort Dodge","Barbara","111 Inwood St.","316-111-1234"
1021,1111,1000,"12-JAN-95","15-JAN-95",67226,"Wichita","Charles","123 Main St.","316-636-5555"
1022,2222,1001,"13-FEB-95","20-FEB-95",67226,"Wichita","Bertram","237 Ash Avenue","316-689-5555"
1020,1111,1000,"10-DEC-94","12-DEC-94",67226,"Wichita","Charles","123 Main St.","316-636-5555"





