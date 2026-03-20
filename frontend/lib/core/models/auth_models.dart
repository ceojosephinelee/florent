import 'package:freezed_annotation/freezed_annotation.dart';

part 'auth_models.freezed.dart';
part 'auth_models.g.dart';

@freezed
class KakaoLoginResult with _$KakaoLoginResult {
  const factory KakaoLoginResult({
    required String accessToken,
    required String refreshToken,
    String? role,
    required bool isNewUser,
    @Default(false) bool hasFlowerShop,
  }) = _KakaoLoginResult;

  factory KakaoLoginResult.fromJson(Map<String, dynamic> json) =>
      _$KakaoLoginResultFromJson(json);
}

@freezed
class ReissueResult with _$ReissueResult {
  const factory ReissueResult({
    required String accessToken,
    required String refreshToken,
    String? role,
    @Default(false) bool hasFlowerShop,
  }) = _ReissueResult;

  factory ReissueResult.fromJson(Map<String, dynamic> json) =>
      _$ReissueResultFromJson(json);
}

@freezed
class SellerInfoResult with _$SellerInfoResult {
  const factory SellerInfoResult({
    required int sellerId,
    required String shopName,
  }) = _SellerInfoResult;

  factory SellerInfoResult.fromJson(Map<String, dynamic> json) =>
      _$SellerInfoResultFromJson(json);
}
