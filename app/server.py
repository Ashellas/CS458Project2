from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/submit_survey', methods=['POST'])
def submit_survey():
    print("Received data:", request.json)
    return jsonify({"status": "success"})

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
