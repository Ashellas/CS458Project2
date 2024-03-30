from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/submit_survey', methods=['POST'])
def submit_survey():
    data = request.json
    print("Received survey data:", data)
    return jsonify({"status": "success", "message": "Data received"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
