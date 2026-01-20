# Freelancer Hiring Platform 🚀

**Freelancer Hiring Platform** is a high-performance, Java-based simulation engine designed to match clients with freelancers in real-time.

This project focuses on **algorithmic efficiency** and **low-level data structure implementation**. Instead of relying on the standard Java Collections Framework, I built the core data structures (HashMaps and Priority Queues) from scratch to optimize for specific performance constraints and to demonstrate a deep understanding of memory management and algorithmic complexity.

## 📋 Project Overview

The platform simulates a dynamic gig economy marketplace. It handles thousands of user interactions, processing job requests, financial transactions, and reputation systems instantly.

The core challenge addressed in this project is **scalability**. The system must efficiently rank and retrieve the best freelancer for a specific job from a massive pool of candidates, while simultaneously updating dynamic attributes like "burnout" status, skill levels, and customer loyalty tiers.

## 🏗 Technical Architecture & Custom Data Structures

To ensure the system runs with **O(1)** and **O(log N)** time complexities for critical operations, I implemented the following custom structures:

### 1. Custom Generic HashMap (`hashmap.java`)

* **Purpose:** Provides instant **O(1)** access to User entities (Customers and Freelancers) via ID.
* **Implementation:** A generic `<K, V>` hash map using separate chaining (linked lists) to handle collisions efficiently.
* **Why Custom?** To have full control over the hashing mechanism and load factor management without the overhead of the standard library.

### 2. Binary Heap Priority Queue (`PriorityQueue.java`)

* **Purpose:** Efficiently ranks and retrieves the top freelancers for specific service categories in **O(log N)** time.
* **Implementation:** An array-based min/max heap that dynamically reorders freelancers based on a complex "Composite Score."
* **Why Custom?** The standard `java.util.PriorityQueue` does not support efficient `update` operations (changing a freelancer's score requires re-heapifying). My implementation supports `percolateUp` and `percolateDown` operations to handle dynamic score changes in real-time.

## 🧠 Smart Matching Algorithm

The engine doesn't just match based on price. It utilizes a weighted **Composite Score** algorithm to find the *best fit* for a job.

* **Skill Match:** A dot-product calculation comparing the freelancer's 5-dimensional skill vector (Technical, Communication, Creativity, Efficiency, Detail) against the job's requirements.
* **Reliability:** A metric derived from the ratio of completed jobs vs. cancelled jobs.
* **Burnout Penalty:** A dynamic penalty applied if a freelancer takes on too many jobs in a month, simulating fatigue and reduced performance.

## ✨ Key Features

* **Dynamic Skill Evolution:** Freelancers "level up" their skills (Technical, Soft Skills, etc.) after receiving high ratings (4.0+) and lose skill points if they cancel jobs.
* **Economy & Loyalty System:** Customers earn status tiers (Bronze, Silver, Gold, Platinum) based on total spending, unlocking platform subsidies and discounts.
* **Burnout Simulation:** The system tracks monthly job volume. Overworking freelancers triggers a "Burnout" state, significantly lowering their ranking in the search algorithm until they recover.
* **Blacklist Management:** Supports both user-defined blacklisting (a customer blocking a specific freelancer) and system-wide bans for unreliable users.

## 🚀 Installation & Usage

### Prerequisites

* Java JDK (11 or higher)
* Python 3 (for the test runner)

### Compilation

The source code is located in the `src` directory. Compile the custom classes and the main engine:

```bash
cd src
javac *.java

```

### Running the Engine

The system accepts an input file containing simulation commands and outputs the logs to a file.

```bash
java Main <input_file_path> <output_file_path>

```

**Example:**

```bash
java Main input.txt output.txt

```

### Input File Format Example

```text
register_customer client_01
register_freelancer dev_007 web_dev 200 90 85 80 95 90
request_job client_01 web_dev 1
complete_and_rate dev_007 5
simulate_month

```

## 🧪 Automated Testing

I developed a Python script (`test_runner.py`) to automate integration testing. It runs the Java engine against various test cases and compares the actual output with the expected logs to ensure logical correctness.

To run the full test suite:

```bash
python test_runner.py --verbose

```

## 📂 File Structure

```
FreelancerHiringPlatform/
├── src/
│   ├── Main.java           # Entry point
│   ├── GigMatchPro.java    # Engine Controller (Business Logic)
│   ├── hashmap.java        # Custom Data Structure 
│   ├── PriorityQueue.java  # Custom Data Structure
│   ├── Freelancer.java     # User Entity & Score Logic
│   └── ...
├── test_cases/             # Input/Output scenarios
└── test_runner.py          # Integration test script

```

---
