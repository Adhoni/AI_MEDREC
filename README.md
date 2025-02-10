# AI-MED-Rec

It is an Android application that allows users to upload text, images, or audio to a backend API built with Django. The backend processes these inputs based on LLM to extract prescriptions, dosages, and other medical details, which are then displayed in the Android app.

## Features

- Upload medical data using text, images, or audio.
- Interact with a Django-based backend API for processing.
- Display extracted medical details such as prescriptions and dosages.

## Screen-grabs

## Main Screen
![image](https://github.com/user-attachments/assets/65cc12b1-2df5-4e1b-8b98-79b3399d43c3)


## Image 
![image](https://github.com/user-attachments/assets/333c1121-663c-45ee-a41c-0d125e99f117)
![image](https://github.com/user-attachments/assets/48205549-e030-4c2c-a5af-ea079d26c310)


## Audio 
![image](https://github.com/user-attachments/assets/e9f33756-6656-4764-88f6-e9168913cf91)

## File
![image](https://github.com/user-attachments/assets/ca5fea40-638c-463e-8f85-d4386452808b)
![image](https://github.com/user-attachments/assets/91f3aea5-8eeb-4ee2-826d-267c4a342fc6)



## Installation

### Prerequisites

- Android Studio - Hedgehog (Recommended)
- Python 3.x
- Django

### Frontend (Android)

1. Clone the repository:
    ```sh
    git clone https://github.com/Adhoni/Ai-Med-Rec-FE.git
    ```

2. Open the project in Android Studio.

3. Build the project and run it on an emulator or physical device.



## Usage

1. Open the app on your Android device.
2. Choose an upload method:
    - **Text:** Get medical details through text format.
    - **Image-Input:** Capture or upload an image of a medical document.
    - **Audio:** Record or upload an audio file containing medical information.
3. Submit the data.
4. The app will send the data to the backend API.
5. The processed medical details will be displayed in the app.

## Steps for contribution


1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature-name`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature-name`).
5. Open a pull request.


# Running the App on a Tablet

## Steps to Run the App

### 1. Connect the Tablet
- Use a **USB cable** to connect your tablet to your computer.
- Enable **Developer Mode** and **USB Debugging** on the tablet.

### 2. Update IP Addresses in the Code
- Locate the **backend and frontend** configurations where the **IP address** .
- Replace the existing IP with **your machine's IP** at all places where IP address is included (you can find it using `ipconfig` on Windows or `ifconfig` on macOS/Linux).
- Ensure both the **backend and frontend** refer to the **same updated IP**.

### 3. Run the Backend First
- Follow **Snigdha's instructions** to start the backend.
- Run the backend service using the required commands.
- Check logs to confirm that the server is running correctly.

### 4. Run the Android App
- Open **Android Studio**.
- Click on the **Run button** (or use `Shift + F10`).
- Select the connected **tablet** as the deployment target.
- The app should launch on the tablet and communicate with the backend.

---
✅ If you encounter any issues, check logs and verify network connectivity.



## Contact

For any questions or feedback, please contact:

- Name: Ambalika Dhoni
- Email: ambalika.dhoni@stonybrook.edu

