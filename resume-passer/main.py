from fastapi import FastAPI, UploadFile, File, HTTPException
import pdfplumber
import re
from io import BytesIO

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Resume Parser API"}

@app.post("/parse-resume")
async def parse_resume(file: UploadFile = File(...)):
    # Validate file type
    if not file.filename.lower().endswith('.pdf'):
        raise HTTPException(status_code=400, detail="Only PDF files are supported")

    # Read the PDF file
    try:
        contents = await file.read()
        with pdfplumber.open(BytesIO(contents)) as pdf:
            text = ""
            for page in pdf.pages:
                text += page.extract_text() or ""
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error reading PDF: {str(e)}")

    # Extract skills using a simple keyword-based approach
    skills = extract_skills(text)
    return {"filename": file.filename, "skills": skills}

def extract_skills(text):
    # Define a list of common skills (expand this as needed)
    skill_keywords = [
        "Java", "Python", "JavaScript", "SQL", "HTML", "CSS", "React", "Spring", "Django",
        "AWS", "Docker", "Kubernetes", "Git", "MySQL", "PostgreSQL", "C++", "C#"
    ]

    # Convert text to lowercase for case-insensitive matching
    text = text.lower()
    found_skills = []

    # Look for skills in the text
    for skill in skill_keywords:
        if re.search(r'\b' + skill.lower() + r'\b', text):
            found_skills.append(skill)

    return found_skills if found_skills else ["No skills detected"]

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5897)