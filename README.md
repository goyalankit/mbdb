I have implemented the following commands:

Files Included:
 src
 mdb.jar

Database name has been hardcoded to "mydbenv"

To run:

    create the mydbenv using:
    $ mkdir mydbenv

    To run the script:
    $ java -jar mdb.jar -f script.sql

    To run the console:    
    $ java -jar mdb.jar
      mdb>

#create a table
create table emp ( name str, age int );

#insert into a table
insert into emp values ("jon", 17);

#select from a table
select * from emp;
