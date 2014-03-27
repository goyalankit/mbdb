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

Exit command will abort and takes you out of the mdb shell.

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


For the benchmarking please run it without -d or -p. Using default options.

Optimizations
---

1. Index support added to the database.
2. Database now supports Hash Join algorithm.
3. Relation metadata and Index metadata is cached. This improves the insertion time of large number of tuples. Since no additional query
   needs to be made in order to parse each tuple.
4. Additional sequence DB used to generate ids to keep data consistent in case of concurrent transactions(not implemented).
5. Since Retrieval is the main bottleneck. I decided to use Multiple threads to fetch data.(no improvement, however functionality
   is present and can be enabled using -p flag)
6. Smart hashing. Hashing minimum possible data for hash join algorithm allowing it to run for large datasets.


---

Parallel Database(NOTE: A special `-p` flag needs to be passed while fetching data using parallel method.)

Didn't get any performance benefit. Overhead of threads is much higher. That is why it's off by default.