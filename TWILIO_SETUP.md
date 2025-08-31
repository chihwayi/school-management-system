# Twilio WhatsApp Integration Setup Guide

This guide will help you set up Twilio WhatsApp integration for your school management system to support:
1. **Attendance Notifications** - Sending messages to guardians when students are absent
2. **Assignment Distribution** - Sending assignment documents to entire classes

## Prerequisites

- Twilio Account (Sign up at https://www.twilio.com/)
- WhatsApp Business API access
- Valid phone numbers for testing

## Step 1: Get Twilio Credentials

### 1.1 Create Twilio Account
1. Go to https://www.twilio.com/
2. Sign up for a free account
3. Verify your email and phone number

### 1.2 Get Account SID and Auth Token
1. Log into your Twilio Console
2. Go to **Dashboard** → **Account Info**
3. Copy your **Account SID** and **Auth Token**

### 1.3 Set Up WhatsApp Sandbox (for testing)
1. In Twilio Console, go to **Messaging** → **Try it out** → **Send a WhatsApp message**
2. Follow the instructions to join your WhatsApp sandbox
3. Note your sandbox phone number (usually +14155238886)

## Step 2: Configure Application

### 2.1 Update Application Properties
Edit `school-management-system/src/main/resources/application.properties`:

```properties
# Twilio WhatsApp Configuration
twilio.account.sid=your-actual-account-sid
twilio.auth.token=your-actual-auth-token
twilio.whatsapp.from=whatsapp:+14155238886
twilio.whatsapp.enabled=true
twilio.whatsapp.retry.attempts=3
twilio.whatsapp.retry.delay=5000
```

### 2.2 Environment Variables (Production)
For production, use environment variables:

```bash
export TWILIO_ACCOUNT_SID=your-actual-account-sid
export TWILIO_AUTH_TOKEN=your-actual-auth-token
export TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
export TWILIO_WHATSAPP_ENABLED=true
```

## Step 3: Test the Integration

### 3.1 Test Attendance Notifications
1. **Add student with WhatsApp number**:
   - Go to Students → Register New Student
   - Add a WhatsApp number for the student
   - Add guardian with WhatsApp number

2. **Mark student as absent**:
   - Go to Attendance → Mark Attendance
   - Mark a student as absent
   - Check if WhatsApp message is sent to guardian

### 3.2 Test Assignment Distribution
1. **Upload assignment document**:
   - Go to AI Assistant → Content
   - Generate AI content or upload document
   - Click WhatsApp share button
   - Select class and share

2. **Distribute via API**:
   ```bash
   curl -X POST http://localhost:8080/api/assignments/upload-and-distribute \
     -F "file=@assignment.pdf" \
     -F "title=Math Assignment 1" \
     -F "subjectName=Mathematics" \
     -F "form=Form 2" \
     -F "section=A" \
     -F "academicYear=2025" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

## Step 4: Production Setup

### 4.1 WhatsApp Business API
For production use, you need WhatsApp Business API:

1. **Apply for WhatsApp Business API**:
   - Contact Twilio sales team
   - Provide business verification documents
   - Get approved phone number

2. **Update configuration**:
   ```properties
   twilio.whatsapp.from=whatsapp:+1234567890
   twilio.whatsapp.webhook.url=https://your-domain.com/api/twilio/webhook
   ```

### 4.2 Webhook Setup (Optional)
For message delivery status:

1. **Create webhook endpoint**:
   ```java
   @PostMapping("/twilio/webhook")
   public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
       // Handle delivery status updates
       return ResponseEntity.ok("OK");
   }
   ```

2. **Configure in Twilio Console**:
   - Go to Messaging → Settings → WhatsApp
   - Set webhook URL to your endpoint

## Step 5: Message Templates

### 5.1 Attendance Notification Template
```
📚 Attendance Notification

Dear Parent/Guardian,

We would like to inform you that your child *John Doe* (ID: STU001) was marked *absent* from school today (28/08/2025).

Class: Form 2 A
Date: 28/08/2025

If this is an error or if you have any concerns, please contact the school immediately.

Thank you,
School Administration
```

### 5.2 Assignment Distribution Template
```
📝 New Assignment Document

Subject: Mathematics
Title: Algebra Fundamentals
Teacher: Jane Smith
Date: 28/08/2025 14:30

Please find the assignment document attached.

Complete and submit by the due date.

Good luck! 📚
```

## Step 6: Troubleshooting

### 6.1 Common Issues

**Issue**: "WhatsApp is disabled" in logs
**Solution**: Check `twilio.whatsapp.enabled=true` in properties

**Issue**: "Twilio WhatsApp is not properly configured"
**Solution**: Verify Account SID and Auth Token are correct

**Issue**: Messages not delivered
**Solution**: 
- Check phone number format (should be +263XXXXXXXXX)
- Verify recipient has joined WhatsApp sandbox
- Check Twilio console for error details

### 6.2 Debug Mode
Enable debug logging:

```properties
logging.level.com.devtech.school_management_system.service.WhatsAppService=DEBUG
logging.level.com.twilio=DEBUG
```

### 6.3 Test Phone Numbers
For testing, use these formats:
- Zimbabwe: +263771234567
- International: +1234567890

## Step 7: Cost Management

### 7.1 Pricing
- **WhatsApp Sandbox**: Free for testing
- **WhatsApp Business API**: Pay per message
- **Document Media**: Additional cost for file uploads

### 7.2 Monitoring
1. **Check usage in Twilio Console**:
   - Go to Analytics → Usage
   - Monitor message delivery rates
   - Track costs

2. **Set up alerts**:
   - Configure spending limits
   - Set up usage notifications

## Step 8: Security Best Practices

### 8.1 Credential Management
- Never commit credentials to version control
- Use environment variables in production
- Rotate auth tokens regularly

### 8.2 Rate Limiting
- Implement rate limiting for API calls
- Monitor message frequency
- Respect WhatsApp rate limits

### 8.3 Data Privacy
- Only send messages to verified numbers
- Respect opt-out requests
- Comply with local messaging regulations

## Step 9: Advanced Features

### 9.1 Bulk Messaging
For sending to multiple classes:

```java
// Send to multiple classes
List<String> classes = Arrays.asList("Form 1 A", "Form 1 B", "Form 2 A");
assignmentDocumentService.distributeToMultipleClasses(file, title, classes);
```

### 9.2 Scheduled Messages
For future assignments:

```java
// Schedule message for later
@Scheduled(fixedDelay = 60000) // Check every minute
public void sendScheduledMessages() {
    // Send scheduled assignments
}
```

### 9.3 Message Templates
Create reusable templates:

```java
public enum MessageTemplate {
    ATTENDANCE_ABSENT,
    ASSIGNMENT_NEW,
    ASSIGNMENT_REMINDER
}
```

## Support

For issues with:
- **Twilio Setup**: Contact Twilio Support
- **Application Integration**: Check application logs
- **WhatsApp Business API**: Contact Twilio Sales

## Next Steps

1. **Test with sandbox** numbers first
2. **Apply for WhatsApp Business API** for production
3. **Set up monitoring** and alerts
4. **Train teachers** on using the system
5. **Monitor usage** and costs

---

**🎉 Your school management system is now ready for WhatsApp integration!**
