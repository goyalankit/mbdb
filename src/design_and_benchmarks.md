Design
---


1. It is assumed that all the relations fit into the memory.

2. Creation of database is not a part of transaction. BDB transactions doesn't support full cleanup.

For example,

> open "mynewdb";
.
> abort;
.

This will not delete the directory. Since mdb env needs a lock that is stored in this directory
and hence it cannot be deleted at least without putting a dirty hack.


3. open command

If you try to open a database that doesn't exist. A new database will be created for you.

For example,

> open "new_database";
.

A new database with name new_database will be created for you.

4. Exit;
.

Exit command takes you out of the mdb shell.

USAGE INSTRUCTIONS
---

//To launch in debug mode (More info will be printed in this mode.)
java -jar mdb.jar -d
java -jar mdb.jar -f "filename" -d

//To launch in parallel retrieval mode
java -jar mdb.jar -p

//To launch mdb shell without debug
java -jar mdb.jar
java -jar mdb.jar -f "filename"


Please

Optimizations
---

1. Index support added to the database.
2. Database now supports Hash Join algorithm.
3. Relation metadata is cached. This improves the insertion time of large number of tuples. Since no additional query
   needs to be made in order to parse each tuple.
4. Smart hashing. Hashing minimum possible data
   for hash join algorithm allowing it to run for large datasets.
5. Additional sequence DB used to generate ids to keep data consistent in case of concurrent transactions(not implemented).
6. Since Retrieval is the main bottleneck. I decided to use Multiple threads to fetch data.(no improvement, however functionality
 is present and can be enabled using -p flag)


Benchmarks:
---

Number of Relations: 3
Tuples in Each Relation: 10000


**Index**

select * from products where p_id=15688;

Without Index: 57746ms
With Index: 14ms
Output: 1 Tuple

select * from products where p_id=12101;
WIthout Index: 87348ms
With Index: 9ms

---------------------------------------------------

**Hash Join**
select * from buyer, products_sale, products where products_sale.pid=products.p_id and buyer.buyer_id=products_sale.buyerid;
.

Note: Join between 3 relations of 10,000 size each without any local predicate.

Join timings exclude the retrieval since OS level caching

Hash join: 212 ms
Tuples Returned: 9994

Natural join: 18118 ms
Tuples Returned: 9994

----------------------------------------------------

Parallel Database(NOTE: A special `-p` flag needs to be passed while fetching data using parallel method.)

Didn't get any performance benefit. Overhead of threads is much higher. That is why it's off by default.

