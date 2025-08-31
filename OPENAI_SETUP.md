# 🤖 OpenAI API Setup Guide

## **Step 1: Get Your OpenAI API Key**

1. Go to [OpenAI Platform](https://platform.openai.com/)
2. Sign up or log in to your account
3. Navigate to "API Keys" in your dashboard
4. Click "Create new secret key"
5. Give it a name (e.g., "School Management System")
6. Copy the generated key (it starts with `sk-`)

## **Step 2: Configure the API Key**

### **Option A: Update application.properties (Recommended for Development)**

1. Open the file: `school-management-system/src/main/resources/application.properties`
2. Find the line: `ai.openai.api-key=your-openai-api-key-here`
3. Replace `your-openai-api-key-here` with your actual API key:
   ```properties
   ai.openai.api-key=sk-your-actual-api-key-here
   ```

### **Option B: Environment Variable (Recommended for Production)**

Set the environment variable:
```bash
export AI_OPENAI_API_KEY=sk-your-actual-api-key-here
```

Or add it to your Docker environment:
```yaml
# In docker-compose.yml
environment:
  - AI_OPENAI_API_KEY=sk-your-actual-api-key-here
```

## **Step 3: Restart the Application**

After adding your API key, restart the backend:

```bash
docker-compose restart backend
```

## **Step 4: Test the Integration**

1. Open your browser and go to `http://localhost:3000`
2. Log in as a teacher
3. Navigate to **AI Assistant → Generate Content**
4. Upload some educational resources (syllabus, books, etc.)
5. Generate content - it will now use the real OpenAI API!

## **Configuration Options**

You can customize the AI behavior by modifying these settings in `application.properties`:

```properties
# AI Model (gpt-4o-mini, gpt-4, gpt-3.5-turbo)
ai.openai.model=gpt-4o-mini

# Maximum tokens per request
ai.openai.max-tokens=4000

# Creativity level (0.0 = very focused, 1.0 = very creative)
ai.openai.temperature=0.7

# API timeout in milliseconds
ai.openai.timeout=30000
```

## **Cost Management**

- **GPT-4o-mini**: ~$0.00015 per 1K input tokens, ~$0.0006 per 1K output tokens
- **GPT-4**: ~$0.03 per 1K input tokens, ~$0.06 per 1K output tokens
- **GPT-3.5-turbo**: ~$0.0015 per 1K input tokens, ~$0.002 per 1K output tokens

The system automatically logs usage and costs for monitoring.

## **Security Notes**

⚠️ **Important Security Considerations:**

1. **Never commit your API key to version control**
2. **Use environment variables in production**
3. **Monitor your OpenAI usage and costs**
4. **Set up billing alerts in your OpenAI account**

## **Troubleshooting**

### **Common Issues:**

1. **"Invalid API key" error**
   - Check that your API key is correct and starts with `sk-`
   - Ensure you have sufficient credits in your OpenAI account

2. **"Rate limit exceeded" error**
   - Wait a few minutes and try again
   - Consider upgrading your OpenAI plan

3. **"Model not found" error**
   - Check that the model name in `ai.openai.model` is correct
   - Ensure you have access to the specified model

### **Testing the Connection:**

You can test if your API key is working by making a simple request:

```bash
curl -X POST https://api.openai.com/v1/chat/completions \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4o-mini",
    "messages": [{"role": "user", "content": "Hello!"}],
    "max_tokens": 10
  }'
```

## **Support**

If you encounter any issues:
1. Check the backend logs: `docker-compose logs backend`
2. Verify your API key is correctly configured
3. Ensure you have sufficient OpenAI credits

---

**🎉 You're all set! Your school management system now has real AI-powered content generation!**
