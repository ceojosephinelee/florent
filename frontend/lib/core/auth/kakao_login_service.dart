import 'package:kakao_flutter_sdk_user/kakao_flutter_sdk_user.dart';

/// 카카오 SDK를 호출하여 카카오 Access Token을 반환하는 서비스.
/// 카카오톡 설치 시 → 카카오톡 로그인, 미설치 시 → 카카오 계정(웹) 로그인.
class KakaoLoginService {
  Future<String> login() async {
    OAuthToken token;
    if (await isKakaoTalkInstalled()) {
      token = await UserApi.instance.loginWithKakaoTalk();
    } else {
      token = await UserApi.instance.loginWithKakaoAccount();
    }
    return token.accessToken;
  }
}
