# jdbf

`jdbf` is a library for reading dBASE files, specifically supporting the main `.DBF` file format in the following versions:

- dBASE 5
- dBASE 7

### Limitations
The library does not support:

- Binary fields
- Memo fields
- OLE fields

These unsupported fields are typically stored in `.DBT` files or other related file formats such as `.MDX`.

### Background
This library was developed to fulfill the author's specific requirements.

### Usage
To use the `jdbf` library, follow these steps:

1. **Creating a Reader**:
   - Create a `JDBFReader` instance using the `Builder` class. You can initialize it with a file name, a `File` object, or a byte array.
   ```java
   JDBFReader reader = new JDBFReader.Builder("path/to/file.dbf").build();
   ```

2. **Customizing Charset**:
   - You can specify a custom charset provider if required.
   - Default charset is `ISO-8859-1`.
   ```java
   Map<JDBFSupportedDbaseVersion, String> dbfEncoding = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, "CP852",
            JDBFSupportedDbaseVersion.DBASE_VII, "CP1250"
   );
   JDBFCharsetProvider myCharsetProvider = dbfEncoding::get;
   JDBFReader reader = new JDBFReader.Builder("path/to/file.dbf")
       .withCharsetProvider(myCharsetProvider)
       .build();
   ```

3. **Reading Data**:
    - Access the headers using:
      ```java
      int headerSize = reader.getHeaderSize();
      
      JHeaderField header = reader.getHeader("ColumnName");
      
      int position = 5;
      JHeaderField header = reader.getHeader(position);
      ```
    - Access rows and their values:
      ```java
      int rowCount = reader.getRowCount();
      
      int rowIndex = 2;
      Object value = reader.getValue("ColumnName", rowIndex);
      
      int position = 5;
      Object value = reader.getValue(position, rowIndex);
      ```

4. **Handling Unsupported Features**:
   Note that binary, memo, and OLE fields are not supported, so ensure your `.DBF` file does not rely on these features.

### Additional Information
For detailed information, see the specifications in the following files:
- [dBASE 5 specification](docs/dbf5_spec.md)
- [dBASE 7 specification](docs/dbf7_spec.md)

# License

    Copyright 2024-2025 David Pilar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
