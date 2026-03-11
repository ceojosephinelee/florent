// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'auth_models.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$KakaoLoginResultImpl _$$KakaoLoginResultImplFromJson(
  Map<String, dynamic> json,
) => _$KakaoLoginResultImpl(
  accessToken: json['accessToken'] as String,
  refreshToken: json['refreshToken'] as String,
  role: json['role'] as String?,
  isNewUser: json['isNewUser'] as bool,
);

Map<String, dynamic> _$$KakaoLoginResultImplToJson(
  _$KakaoLoginResultImpl instance,
) => <String, dynamic>{
  'accessToken': instance.accessToken,
  'refreshToken': instance.refreshToken,
  'role': instance.role,
  'isNewUser': instance.isNewUser,
};

_$ReissueResultImpl _$$ReissueResultImplFromJson(Map<String, dynamic> json) =>
    _$ReissueResultImpl(
      accessToken: json['accessToken'] as String,
      refreshToken: json['refreshToken'] as String,
    );

Map<String, dynamic> _$$ReissueResultImplToJson(_$ReissueResultImpl instance) =>
    <String, dynamic>{
      'accessToken': instance.accessToken,
      'refreshToken': instance.refreshToken,
    };

_$SellerInfoResultImpl _$$SellerInfoResultImplFromJson(
  Map<String, dynamic> json,
) => _$SellerInfoResultImpl(
  sellerId: (json['sellerId'] as num).toInt(),
  shopName: json['shopName'] as String,
);

Map<String, dynamic> _$$SellerInfoResultImplToJson(
  _$SellerInfoResultImpl instance,
) => <String, dynamic>{
  'sellerId': instance.sellerId,
  'shopName': instance.shopName,
};
