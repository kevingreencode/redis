// Header section 
52 45 44 49 53 30 30 31 31      // Magic string + version number (ASCII): "REDIS0011".

// Metadata section 
FA                              // Indicates the start of a metadata subsection.
09                              // Length
72 65 64 69 73 2D 76 65 72      // The name of the metadata attribute (string encoded): "redis-ver".
05                              // Length 
37 2E 32 2E 30                  // "7.2.0"

FA                              // Indicates the start of a metadata subsection.
0A                              // Length
72 65 64 69 73 2D 62 69 74 73   // "redis-bits" 
C0 40                           // @


FE                              // Database section
00                              // The index of the database (size encoded). Here, the index is 0.

FB                              // Indicates that hash table size information follows.
01                              // The size of the hash table that stores the keys and value 
00                              // The size of the hash table that stores the expires of the keys


00                              // The 1-byte flag that specifies the value’s type and encoding. 00 means string
05                              // Length
6D 61 6E 67 6F                  // mango
06                              // Length
62 61 6E 61 6E 61               // banana


FF                              // End of the RDB file
D6 AF 65 D8 51 69 DB 61 0A      // Checksum