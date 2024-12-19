# Data File Header Structure for the dBASE Version 7 Table File

**Note:** Unless prefaced by "0x", all numbers specified in the Description column of the following tables are decimal.

## 1.1 Table File Header

| Byte  | Contents      | Description                                                                                                                                           |
|-------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| 0     | 1 byte        | Valid dBASE for Windows table file, bits 0-2 indicate version number: 3 for dBASE Level 5, 4 for dBASE Level 7.                                       |
| 1-3   | 3 bytes       | Date of last update (YYMMDD format). Each byte contains the number as binary. YY has possible values from 0x00-0xFF, allowing a range from 1900-2155. |
| 4-7   | 32-bit number | Number of records in the table (Least significant byte first).                                                                                        |
| 8-9   | 16-bit number | Number of bytes in the header (Least significant byte first).                                                                                         |
| 10-11 | 16-bit number | Number of bytes in the record (Least significant byte first).                                                                                         |
| 12-13 | 2 bytes       | Reserved; filled with zeros.                                                                                                                          |
| 14    | 1 byte        | Flag indicating incomplete dBASE IV transaction.                                                                                                      |
| 15    | 1 byte        | dBASE IV encryption flag.                                                                                                                             |
| 16-27 | 12 bytes      | Reserved for multi-user processing.                                                                                                                   |
| 28    | 1 byte        | Production MDX flag; 0x01 if a production .MDX file exists for this table; 0x00 if no .MDX file exists.                                               |
| 29    | 1 byte        | Language driver ID.                                                                                                                                   |
| 30-31 | 2 bytes       | Reserved; filled with zeros.                                                                                                                          |
| 32-63 | 32 bytes      | Language driver name.                                                                                                                                 |
| 64-67 | 4 bytes       | Reserved.                                                                                                                                             |
| 68-n  | 48 bytes each | Field Descriptor Array (see 1.2).                                                                                                                     |
| n+1   | 1 byte        | 0x0D stored as the Field Descriptor terminator.                                                                                                       |
| n+2   | See below     | Field Properties Structure                                                                                                                            |

## 1.2 Field Descriptor Array (One for each field in the table)

| Byte  | Contents | Description                                                                                             |
|-------|----------|---------------------------------------------------------------------------------------------------------|
| 0-31  | 32 bytes | Field name in ASCII (zero-filled).                                                                      |
| 32    | 1 byte   | Field type in ASCII (B, C, D, N, L, M, @, I, +, F, 0 or G).                                             |
| 33    | 1 byte   | Field length in binary.                                                                                 |
| 34    | 1 byte   | Field decimal count in binary.                                                                          |
| 35-36 | 2 bytes  | Reserved.                                                                                               |
| 37    | 1 byte   | Production .MDX field flag; 0x01 if field has an index tag in the production .MDX file; 0x00 otherwise. |
| 38-39 | 2 bytes  | Reserved.                                                                                               |
| 40-43 | 4 bytes  | Next Autoincrement value if the Field type is Autoincrement, 0x00 otherwise.                            |
| 44-47 | 4 bytes  | Reserved.                                                                                               |

## 1.3 Field Properties Structure

The Field Properties structure contains a header describing the Field Properties array, followed by the actual array,
followed by property data. It comes immediately after the Field Descriptor terminator.

| Byte  | Contents      | Description                                                                        |
|-------|---------------|------------------------------------------------------------------------------------|
| 0-1   | 16-bit number | Number of Standard Properties.                                                     |
| 2-3   | 16-bit number | Start of Standard Property Descriptor Array (see 1.3.1).                           |
| 4-5   | 16-bit number | Number of Custom Properties.                                                       |
| 6-7   | 16-bit number | Start of Custom Property Descriptor Array (see 1.3.2).                             |
| 8-9   | 16-bit number | Number of Referential Integrity (RI) properties.                                   |
| 10-11 | 16-bit number | Start of RI Property Descriptor Array (see 1.3.3).                                 |
| 12-13 | 16-bit number | Start of data - this points past the Descriptor arrays to data used by the arrays. |
| 14-15 | 16-bit number | Actual size of structure, including data (may have 0x1A at the end).               |

## Table Records

Data records are preceded by one byte:

- A space (`0x20`) if the record is not deleted.
- An asterisk (`0x2A`) if the record is deleted.

Fields are packed into records without field separators or record terminators. The end of the file is marked by a single
byte with the end-of-file marker, an OEM code page character value of `26 (0x1A)`.

## Storage of dBASE Data Types

| Symbol | Data Type     | Description                                                     |
|--------|---------------|-----------------------------------------------------------------|
| B      | Binary        | 10 digits representing a .DBT block number, stored as a string. |
| C      | Character     | Padded with blanks to the width of the field.                   |
| D      | Date          | 8 bytes - date stored as a string in the format `YYYYMMDD`.     |
| N      | Numeric       | Number stored as a string, right justified, padded with blanks. |
| L      | Logical       | 1 byte - initialized to `0x20` (space) otherwise `T` or `F`.    |
| M      | Memo          | 10 digits (bytes) representing a .DBT block number.             |
| @      | Timestamp     | 8 bytes - two longs, first for date, second for time.           |
| I      | Long          | 4 bytes. Leftmost bit indicates sign, 0 is negative.            |
| +      | Autoincrement | Same as a Long.                                                 |
| F      | Float         | Number stored as a string, right justified, padded with blanks. |
| O      | Double        | 8 bytes - no conversions, stored as a double.                   |
| G      | OLE           | 10 digits (bytes) representing a .DBT block number.             |

## Binary, Memo, OLE Fields and .DBT Files

Binary, memo, and OLE fields store data in .DBT files consisting of blocks numbered sequentially (0, 1, 2, etc.). SET
BLOCKSIZE determines the size of each block. The first block in the .DBT file, block 0, is the .DBT file header.

Each binary, memo, or OLE field of each record in the .DBF file contains the number of the block (in OEM code page
values) where the field's data actually begins. If a field contains no data, the .DBF file contains blanks (0x20) rather
than a number.

When data is changed in a field, the block numbers may also change and the number in the .DBF may be changed to reflect
the new location. 