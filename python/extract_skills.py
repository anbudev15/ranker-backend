# extract_skills.py

import sys
import fitz  # PyMuPDF
import docx2txt
import json
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity

def read_file(file_path):
    if file_path.endswith(".pdf"):
        text = ""
        with fitz.open(file_path) as doc:
            for page in doc:
                text += page.get_text()
        return text
    elif file_path.endswith(".docx"):
        return docx2txt.process(file_path)
    else:
        return ""

def extract_skills(text):
    # Sample skill set (replace with dynamic list or NLP)
    skills_list = ['Java', 'Python', 'Spring', 'SQL', 'Docker', 'Kafka', 'AWS']
    found = [skill for skill in skills_list if skill.lower() in text.lower()]
    return found

def calculate_score(jd, resume_text):
    vectorizer = CountVectorizer().fit_transform([jd, resume_text])
    vectors = vectorizer.toarray()
    score = cosine_similarity([vectors[0]], [vectors[1]])[0][0]
    return round(score * 100, 2)

if __name__ == "__main__":
    # 1. Args from Java
    resume_path = sys.argv[1]
    job_desc = sys.argv[2]

    resume_text = read_file(resume_path)
    skills = extract_skills(resume_text)
    score = calculate_score(job_desc, resume_text)

    # 2. Output JSON to Java
    result = {
        "skills": skills,
        "score": score
    }
    print(json.dumps(result))
