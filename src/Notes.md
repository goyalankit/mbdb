### Notes

#### Completed
1. Conjunctive single relation select query is functional.
2. Implemented a cache of relations information.
3. Added support for multiple relations. `Join` relations.
4. implemented update commands
5. implemented delete.
6. implement script command.
7. Index metadata stored in `index_metadata` column with relation name as a key


#### TODO:
0. Check for database open/close commands. Figure out what to do with them.
1. close database later in the program without creating locking problems.
2. Read on index creation and stuff.


#### Issues:


### Test cases:
1. Two relations mentioned but the condition is not given for the join.
`select * from zipcodes, customers  where zipcodes.zip=54444;`

2. zip here is ambiguious
`select * from zipcodes, customers  where zip=54444;`

3. select * from zipcodes, customers, orders where zipcodes.zip = customers.zip and orders.cno = customers.cno;

4. select zipcodes.zip from zipcodes;


### New test cases
1. Check that the cache works properly.
2. Check abort and commit behave properly.


#### Optimizations
1. Relations are cached over committed transactions. Cache is invalidated when a relations aborts.


####Results
select * from zipcodes, customers, orders where zipcodes.zip = customers.zip and orders.cno = customers.cno;
1023,3333,1000,"20-JUN-97","30-SEP-96",60606,"Fort Dodge","Barbara","111 Inwood St.","316-111-1234"
1021,1111,1000,"12-JAN-95","15-JAN-95",67226,"Wichita","Charles","123 Main St.","316-636-5555"
1022,2222,1001,"13-FEB-95","20-FEB-95",67226,"Wichita","Bertram","237 Ash Avenue","316-689-5555"
1020,1111,1000,"10-DEC-94","12-DEC-94",67226,"Wichita","Charles","123 Main St.","316-636-5555"





