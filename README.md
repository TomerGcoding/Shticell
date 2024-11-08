# Shticell - Client-Server Spreadsheet Management Application

**Shticell** is a client-server application designed for managing spreadsheets with user-specific access controls, data manipulation capabilities, and complex cell functions. Built with JavaFX for the client interface and powered by Tomcat and OkHttp, Shticell provides a dynamic platform for collaborative data handling.

## Table of Contents

1. [Features](#features)
2. [Installation](#installation)
3. [Usage](#usage)
4. [Components Overview](#components-overview)

---

## Features

- **User-Specific Access Control**:
    - Each sheet has restricted permissions based on user roles. The *Owner* (user who uploads the sheet XML) manages permissions for all users.
    - Users can view their current permission level, request upgrades, and, if they are the *Owner*, approve or reject permission requests.

- **Sheet Management**:
    - Load XML files to populate the Sheets table, which displays all available sheets, their names, owners, sizes, and access permissions.
    - Sheets are openable in read-only or editable modes, depending on user permissions.
    - Access Permissions Table displays the permissions status for each user on the selected sheet, including:
        - **User Name**
        - **Current Permission**
        - **Requested Permission**
        - **Permission Status**
        - Owners can approve or reject requests for access changes when a request is pending.

- **Sheet Operations**:
    - In the sheet editing view, users can manipulate data using sorting, filtering, range selection, and complex expressions.
    - Filtering allows selecting multiple values per column to tailor data views.

- **Data Functions**:
    - Built-in support for various data functions allows users to create expressions within cells for calculations, logical comparisons, and more.
    - **Syntax**: `{<function name>, <arg 1>, <arg 2>, …, <arg n>}`
    - Functions are nested to enable advanced expressions and handle multiple argument types.
    - **Categories**:
        - **Logical**: Functions like `EQUAL`, `IF`, `AND` for condition-based logic.
        - **Mathematical**: `PLUS`, `SUM`, `AVERAGE` for arithmetic operations and aggregations.
        - **String**: `CONCAT`, `SUB` for handling strings.
        - **System**: `REF` to reference other cells.
    - **Examples**:
        - `{PLUS, 4, 5}` - Adds 4 and 5.
        - `{IF, {EQUAL, {PLUS, 1, 1}, 2}, "Yes", "No"}` - Checks if 1+1 equals 2 and returns "Yes" or "No".

---

## Installation

1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/your-repo/shticell.git
    ```
2. Navigate to the project directory and set up your environment.

### Server Setup (Tomcat)
1. Deploy the server WAR file to your Tomcat instance.
2. Configure the server URL in the client application to point to your Tomcat server.

### Client Setup
1. Use a compatible IDE (e.g., IntelliJ, Eclipse) to import the project.
2. Ensure you have JavaFX and OkHttp dependencies set up correctly.

--- 

## Usage
1. **Start the Server**: Run your Tomcat server with the deployed WAR file.
2. **Run the Client**: Launch the Shticell JavaFX client application.

- **Loading a Sheet:** 
  - Use the Load XML File button to upload and initialize a new sheet. 
- **Requesting and Managing Permissions:** 
  - View permissions for each sheet in the Access Permissions table.
  - Request additional permissions by selecting a desired role and clicking Request Permission.
  -Owners can approve or reject permission requests directly in the permissions table.
   
- **Editing Sheets:**

    - Open sheets where you have read or write permissions.
    - Apply sorting, filtering, and range selections to focus on relevant data.
    - Enter expressions and use built-in functions for data analysis.
    - Note: Editing is only possible when working on the latest sheet version. If you are not working on the most updated version, a Show Latest Version button will appear, allowing you to synchronize to the latest version for editing.
---

## Components Overview
### Sheet Management
- **Sheet Table**: Displays all sheets in the system with details including sheet name, owner, size, and the user’s access level.
- **Access Permissions Table**: Shows user permissions for a selected sheet, with the ability for the owner to manage requests.
- **Request Permission**: Users can request additional access by selecting a role and submitting a request.

### Sheet Operations
- **Data Manipulation**:
  - *Sorting*: Sort data by specific columns.
  - *Filtering*: Filter by selecting multiple values per column.
  - *Range Selection*: Choose a subset of data for focused operations.

### Data Functions
- **Expression-Based Calculations**:
  - Shticell supports a range of logical, mathematical, and string functions.
  - Functions can be nested and combined, offering flexibility in data manipulation directly within cells.
      
---

***Enjoy using Shticell for collaborative spreadsheet management and data analysis!***