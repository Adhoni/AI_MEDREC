## Getting Started

### Prerequisites

Ensure you have Python installed on your system. You can download it from [python.org](https://www.python.org/). We were using Python 3.12.2.

### Install Dependencies

Before running the project, install the required dependencies by executing the following command in your terminal:

```bash
pip install -r requirements.txt
```

### Running the Application Locally

To start the application locally, use the following command:

```bash
python manage.py runserver
```

### Running on a Specific Port

If you wish to run the application on a specific port, use the command format below:

```bash
python manage.py runserver <IPv4-Address:port-number>
```

#### How to Get the IPv4 Address

1. Open Command Prompt.
2. Type the following command and press Enter:

   ```bash
   ipconfig
   ```

3. Locate the `IPv4 Address` under your active network connection. It will look something like this:

   ```
   IPv4 Address. . . . . . . . . . . : 172.24.18.74
   ```

4. Replace `<IPv4-Address:port-number>` with the actual IPv4 address and port number. For example:

   ```bash
   python manage.py runserver 172.24.18.74:8080
   ```
