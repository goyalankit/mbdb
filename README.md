Implementation of MDB(miniature database) that supports a useful subset of SQL. 

**Berkley DB(java edition)** is used a storage engine.

MDB supports the following DML commands:

* `Abort`   -- rollback all updates since the last abort or commit.
* `Close` -- close a database that has no uncommitted updates.  Uncommitted updates must be aborted or committed prior to a close.
* `Commit` -- commit all updates since the last abort or commit.
* `Delete` -- delete the tuples associated with a single-relation (i.e. non-join) predicate.
* `Exit` -- exit MDB.  This will close the currently opened database.
* `Insert` -- insert tuple into database.
* `Open` -- open a database for update and retrieval.  Only one database can be open at any time.
* `Script` -- run the script in the designated file.
* `Select` -- retrieve tuples from one or more relations.
* `Update` -- update zero or more tuples in a single relation.

#### USAGE INSTRUCTIONS

```
//To launch in debug mode (More info will be printed in this mode.) java -jar mdb.jar -d
java -jar mdb.jar -f "filename" -d
￼//To launch in parallel retrieval mode (more on this at the end) java -jar mdb.jar -p
//To launch mdb shell without debug java -jar mdb.jar
java -jar mdb.jar -f "filename"
```
For the benchmarking please run it WITHOUT -d or -p. 
Using default options. Use sample_commands.sql file to create a sample database.



#### Design Decisions

1. It is assumed that all the relations fit into the memory.
2. Creation of database is not a part of transaction. BDB transactions doesn't support
￼full cleanup. For example,
```sh
    > open "mynewdb"; .
    > abort;
```
This will not delete the directory. 
3. `open` command
If you try to open a database that doesn't exist. A new database will be created for
you.
For example,
```
> open "new_database";
```
A new database with name new_database will be created for you. 
4. `Exit;`
Exit command will abort and takes you out of the mdb shell. 



#### Optimizations 
￼
1. Index support added to the database.
2. Database now supports Hash Join algorithm.
3. Caching: Relation metadata and Index metadata is cached. This improves the insertion time of large number of tuples. Since no additional query needs to be made in order to parse each tuple.
4. Additional sequence DB used to generate ids to keep data consistent in case of concurrent transactions(if implemented).
5. Since Retrieval is the main bottleneck. I decided to use Multiple threads to fetch data in parallel.(no significant improvement, however functionality
is present and can be enabled using -p flag)
6. Smart hashing. Hashing minimum possible data for hash join algorithm allowing it to run for large datasets.

Parallel Database(NOTE: A special `-p` flag needs to be passed while fetching data using parallel method.)
Didn't get any performance benefit. Overhead of threads is much higher. That is why it's off by default.
