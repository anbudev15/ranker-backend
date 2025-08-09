from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

def extract_skills_and_score(resume_text, jd_text):
    vectorizer = TfidfVectorizer(stop_words='english')
    tfidf = vectorizer.fit_transform([resume_text, jd_text])
    similarity = cosine_similarity(tfidf[0:1], tfidf[1:2])

    score = round(similarity[0][0] * 100, 2)

    # You can also extract top keywords here
    return {
        "score": score,
        "matchedSkills": [],  # You can list matched keywords
    }
