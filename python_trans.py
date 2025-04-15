!pip install googletrans==4.0.0-rc1


import json
from googletrans import Translator
import os
import time

# Hàm đệ quy để dịch các giá trị trong JSON
def translate_json(data, translator, dest_language='vi'):
    if isinstance(data, dict):
        # Nếu là dictionary, duyệt qua từng key và value
        return {key: translate_json(value, translator, dest_language) for key, value in data.items()}
    elif isinstance(data, list):
        # Nếu là list, duyệt qua từng phần tử
        return [translate_json(element, translator, dest_language) for element in data]
    elif isinstance(data, str):
        # Nếu là chuỗi, thực hiện dịch với xử lý ngoại lệ
        #for attempt in range(1):  # Thử lại tối đa 3 lần nếu gặp lỗi
            try:
                return translator.translate(data, dest=dest_language).text
            except Exception as e:
                print(f"Lỗi khi dịch: {e}")
                #time.sleep(2)  # Chờ 2 giây trước khi thử lại
        # Nếu thử 3 lần mà vẫn lỗi, trả về chuỗi gốc
        #return data
    else:
        # Các loại dữ liệu khác (số, None, ...) thì giữ nguyên
        return data

# Kiểm tra xem file có tồn tại và không rỗng
file_path = 'monan.json'
if not os.path.exists(file_path) or os.path.getsize(file_path) == 0:
    raise ValueError(f"File {file_path} không tồn tại hoặc rỗng.")

# Đọc file JSON
with open(file_path, 'r') as f:
    try:
        data = json.load(f)
    except json.JSONDecodeError:
        raise ValueError("Dữ liệu trong file JSON không hợp lệ.")

# Khởi tạo Translator
translator = Translator()

# Dịch toàn bộ dữ liệu trong file JSON
translated_data = translate_json(data, translator)

# Ghi lại file JSON đã dịch
with open('MonAnTrans.json', 'w') as f:
    json.dump(translated_data, f, ensure_ascii=False, indent=4)

print("File JSON đã được dịch thành công!")
