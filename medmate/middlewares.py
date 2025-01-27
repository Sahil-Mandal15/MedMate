from firebase_admin import auth
from django.http import JsonResponse

class FirebaseAuthMiddleware:

    def __init__(self,get_response):
        self.get_response=get_response

    def __call__(self,request):
        auth_header= request.headers.get("Authorization")
        if auth_header:
            try:
                token = auth_header.split("Bearer ")[1]
                decoded_token = auth.verify_id_token(token)
                request.firebase_user=decoded_token
            except Exception as e:
                return JsonResponse({"error": "invalid or expired token"}, status=401)
        else:
            request.firebase_user= None
        
        return self.get_response(request)