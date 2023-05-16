The two optimizations that I used to make my parsers faster were:
1. I used a hash table to store the information from the two different tables, so that that they were stored together in memory.
2. I also used batch inserts when I tried to insert my files into the database, os that it would run faster.

All in all, these optimizations saved time when we were running the parser, since the data was saved in memory so that we did not have to keep accessing the tables.
In addition, having batch inserts meant that we could insert the data all at once instead of doing it one at a time, which would have slowed it down.