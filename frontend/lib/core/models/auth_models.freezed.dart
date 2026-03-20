// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'auth_models.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

KakaoLoginResult _$KakaoLoginResultFromJson(Map<String, dynamic> json) {
  return _KakaoLoginResult.fromJson(json);
}

/// @nodoc
mixin _$KakaoLoginResult {
  String get accessToken => throw _privateConstructorUsedError;
  String get refreshToken => throw _privateConstructorUsedError;
  String? get role => throw _privateConstructorUsedError;
  bool get isNewUser => throw _privateConstructorUsedError;
  bool get hasFlowerShop => throw _privateConstructorUsedError;

  /// Serializes this KakaoLoginResult to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of KakaoLoginResult
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $KakaoLoginResultCopyWith<KakaoLoginResult> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $KakaoLoginResultCopyWith<$Res> {
  factory $KakaoLoginResultCopyWith(
    KakaoLoginResult value,
    $Res Function(KakaoLoginResult) then,
  ) = _$KakaoLoginResultCopyWithImpl<$Res, KakaoLoginResult>;
  @useResult
  $Res call({
    String accessToken,
    String refreshToken,
    String? role,
    bool isNewUser,
    bool hasFlowerShop,
  });
}

/// @nodoc
class _$KakaoLoginResultCopyWithImpl<$Res, $Val extends KakaoLoginResult>
    implements $KakaoLoginResultCopyWith<$Res> {
  _$KakaoLoginResultCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of KakaoLoginResult
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? accessToken = null,
    Object? refreshToken = null,
    Object? role = freezed,
    Object? isNewUser = null,
    Object? hasFlowerShop = null,
  }) {
    return _then(
      _value.copyWith(
            accessToken: null == accessToken
                ? _value.accessToken
                : accessToken // ignore: cast_nullable_to_non_nullable
                      as String,
            refreshToken: null == refreshToken
                ? _value.refreshToken
                : refreshToken // ignore: cast_nullable_to_non_nullable
                      as String,
            role: freezed == role
                ? _value.role
                : role // ignore: cast_nullable_to_non_nullable
                      as String?,
            isNewUser: null == isNewUser
                ? _value.isNewUser
                : isNewUser // ignore: cast_nullable_to_non_nullable
                      as bool,
            hasFlowerShop: null == hasFlowerShop
                ? _value.hasFlowerShop
                : hasFlowerShop // ignore: cast_nullable_to_non_nullable
                      as bool,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$KakaoLoginResultImplCopyWith<$Res>
    implements $KakaoLoginResultCopyWith<$Res> {
  factory _$$KakaoLoginResultImplCopyWith(
    _$KakaoLoginResultImpl value,
    $Res Function(_$KakaoLoginResultImpl) then,
  ) = __$$KakaoLoginResultImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String accessToken,
    String refreshToken,
    String? role,
    bool isNewUser,
    bool hasFlowerShop,
  });
}

/// @nodoc
class __$$KakaoLoginResultImplCopyWithImpl<$Res>
    extends _$KakaoLoginResultCopyWithImpl<$Res, _$KakaoLoginResultImpl>
    implements _$$KakaoLoginResultImplCopyWith<$Res> {
  __$$KakaoLoginResultImplCopyWithImpl(
    _$KakaoLoginResultImpl _value,
    $Res Function(_$KakaoLoginResultImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of KakaoLoginResult
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? accessToken = null,
    Object? refreshToken = null,
    Object? role = freezed,
    Object? isNewUser = null,
    Object? hasFlowerShop = null,
  }) {
    return _then(
      _$KakaoLoginResultImpl(
        accessToken: null == accessToken
            ? _value.accessToken
            : accessToken // ignore: cast_nullable_to_non_nullable
                  as String,
        refreshToken: null == refreshToken
            ? _value.refreshToken
            : refreshToken // ignore: cast_nullable_to_non_nullable
                  as String,
        role: freezed == role
            ? _value.role
            : role // ignore: cast_nullable_to_non_nullable
                  as String?,
        isNewUser: null == isNewUser
            ? _value.isNewUser
            : isNewUser // ignore: cast_nullable_to_non_nullable
                  as bool,
        hasFlowerShop: null == hasFlowerShop
            ? _value.hasFlowerShop
            : hasFlowerShop // ignore: cast_nullable_to_non_nullable
                  as bool,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$KakaoLoginResultImpl implements _KakaoLoginResult {
  const _$KakaoLoginResultImpl({
    required this.accessToken,
    required this.refreshToken,
    this.role,
    required this.isNewUser,
    this.hasFlowerShop = false,
  });

  factory _$KakaoLoginResultImpl.fromJson(Map<String, dynamic> json) =>
      _$$KakaoLoginResultImplFromJson(json);

  @override
  final String accessToken;
  @override
  final String refreshToken;
  @override
  final String? role;
  @override
  final bool isNewUser;
  @override
  @JsonKey()
  final bool hasFlowerShop;

  @override
  String toString() {
    return 'KakaoLoginResult(accessToken: $accessToken, refreshToken: $refreshToken, role: $role, isNewUser: $isNewUser, hasFlowerShop: $hasFlowerShop)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$KakaoLoginResultImpl &&
            (identical(other.accessToken, accessToken) ||
                other.accessToken == accessToken) &&
            (identical(other.refreshToken, refreshToken) ||
                other.refreshToken == refreshToken) &&
            (identical(other.role, role) || other.role == role) &&
            (identical(other.isNewUser, isNewUser) ||
                other.isNewUser == isNewUser) &&
            (identical(other.hasFlowerShop, hasFlowerShop) ||
                other.hasFlowerShop == hasFlowerShop));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    accessToken,
    refreshToken,
    role,
    isNewUser,
    hasFlowerShop,
  );

  /// Create a copy of KakaoLoginResult
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$KakaoLoginResultImplCopyWith<_$KakaoLoginResultImpl> get copyWith =>
      __$$KakaoLoginResultImplCopyWithImpl<_$KakaoLoginResultImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$KakaoLoginResultImplToJson(this);
  }
}

abstract class _KakaoLoginResult implements KakaoLoginResult {
  const factory _KakaoLoginResult({
    required final String accessToken,
    required final String refreshToken,
    final String? role,
    required final bool isNewUser,
    final bool hasFlowerShop,
  }) = _$KakaoLoginResultImpl;

  factory _KakaoLoginResult.fromJson(Map<String, dynamic> json) =
      _$KakaoLoginResultImpl.fromJson;

  @override
  String get accessToken;
  @override
  String get refreshToken;
  @override
  String? get role;
  @override
  bool get isNewUser;
  @override
  bool get hasFlowerShop;

  /// Create a copy of KakaoLoginResult
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$KakaoLoginResultImplCopyWith<_$KakaoLoginResultImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

ReissueResult _$ReissueResultFromJson(Map<String, dynamic> json) {
  return _ReissueResult.fromJson(json);
}

/// @nodoc
mixin _$ReissueResult {
  String get accessToken => throw _privateConstructorUsedError;
  String get refreshToken => throw _privateConstructorUsedError;
  String? get role => throw _privateConstructorUsedError;
  bool get hasFlowerShop => throw _privateConstructorUsedError;

  /// Serializes this ReissueResult to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of ReissueResult
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $ReissueResultCopyWith<ReissueResult> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ReissueResultCopyWith<$Res> {
  factory $ReissueResultCopyWith(
    ReissueResult value,
    $Res Function(ReissueResult) then,
  ) = _$ReissueResultCopyWithImpl<$Res, ReissueResult>;
  @useResult
  $Res call({
    String accessToken,
    String refreshToken,
    String? role,
    bool hasFlowerShop,
  });
}

/// @nodoc
class _$ReissueResultCopyWithImpl<$Res, $Val extends ReissueResult>
    implements $ReissueResultCopyWith<$Res> {
  _$ReissueResultCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of ReissueResult
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? accessToken = null,
    Object? refreshToken = null,
    Object? role = freezed,
    Object? hasFlowerShop = null,
  }) {
    return _then(
      _value.copyWith(
            accessToken: null == accessToken
                ? _value.accessToken
                : accessToken // ignore: cast_nullable_to_non_nullable
                      as String,
            refreshToken: null == refreshToken
                ? _value.refreshToken
                : refreshToken // ignore: cast_nullable_to_non_nullable
                      as String,
            role: freezed == role
                ? _value.role
                : role // ignore: cast_nullable_to_non_nullable
                      as String?,
            hasFlowerShop: null == hasFlowerShop
                ? _value.hasFlowerShop
                : hasFlowerShop // ignore: cast_nullable_to_non_nullable
                      as bool,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$ReissueResultImplCopyWith<$Res>
    implements $ReissueResultCopyWith<$Res> {
  factory _$$ReissueResultImplCopyWith(
    _$ReissueResultImpl value,
    $Res Function(_$ReissueResultImpl) then,
  ) = __$$ReissueResultImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String accessToken,
    String refreshToken,
    String? role,
    bool hasFlowerShop,
  });
}

/// @nodoc
class __$$ReissueResultImplCopyWithImpl<$Res>
    extends _$ReissueResultCopyWithImpl<$Res, _$ReissueResultImpl>
    implements _$$ReissueResultImplCopyWith<$Res> {
  __$$ReissueResultImplCopyWithImpl(
    _$ReissueResultImpl _value,
    $Res Function(_$ReissueResultImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of ReissueResult
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? accessToken = null,
    Object? refreshToken = null,
    Object? role = freezed,
    Object? hasFlowerShop = null,
  }) {
    return _then(
      _$ReissueResultImpl(
        accessToken: null == accessToken
            ? _value.accessToken
            : accessToken // ignore: cast_nullable_to_non_nullable
                  as String,
        refreshToken: null == refreshToken
            ? _value.refreshToken
            : refreshToken // ignore: cast_nullable_to_non_nullable
                  as String,
        role: freezed == role
            ? _value.role
            : role // ignore: cast_nullable_to_non_nullable
                  as String?,
        hasFlowerShop: null == hasFlowerShop
            ? _value.hasFlowerShop
            : hasFlowerShop // ignore: cast_nullable_to_non_nullable
                  as bool,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$ReissueResultImpl implements _ReissueResult {
  const _$ReissueResultImpl({
    required this.accessToken,
    required this.refreshToken,
    this.role,
    this.hasFlowerShop = false,
  });

  factory _$ReissueResultImpl.fromJson(Map<String, dynamic> json) =>
      _$$ReissueResultImplFromJson(json);

  @override
  final String accessToken;
  @override
  final String refreshToken;
  @override
  final String? role;
  @override
  @JsonKey()
  final bool hasFlowerShop;

  @override
  String toString() {
    return 'ReissueResult(accessToken: $accessToken, refreshToken: $refreshToken, role: $role, hasFlowerShop: $hasFlowerShop)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ReissueResultImpl &&
            (identical(other.accessToken, accessToken) ||
                other.accessToken == accessToken) &&
            (identical(other.refreshToken, refreshToken) ||
                other.refreshToken == refreshToken) &&
            (identical(other.role, role) || other.role == role) &&
            (identical(other.hasFlowerShop, hasFlowerShop) ||
                other.hasFlowerShop == hasFlowerShop));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode =>
      Object.hash(runtimeType, accessToken, refreshToken, role, hasFlowerShop);

  /// Create a copy of ReissueResult
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$ReissueResultImplCopyWith<_$ReissueResultImpl> get copyWith =>
      __$$ReissueResultImplCopyWithImpl<_$ReissueResultImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$ReissueResultImplToJson(this);
  }
}

abstract class _ReissueResult implements ReissueResult {
  const factory _ReissueResult({
    required final String accessToken,
    required final String refreshToken,
    final String? role,
    final bool hasFlowerShop,
  }) = _$ReissueResultImpl;

  factory _ReissueResult.fromJson(Map<String, dynamic> json) =
      _$ReissueResultImpl.fromJson;

  @override
  String get accessToken;
  @override
  String get refreshToken;
  @override
  String? get role;
  @override
  bool get hasFlowerShop;

  /// Create a copy of ReissueResult
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$ReissueResultImplCopyWith<_$ReissueResultImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

SellerInfoResult _$SellerInfoResultFromJson(Map<String, dynamic> json) {
  return _SellerInfoResult.fromJson(json);
}

/// @nodoc
mixin _$SellerInfoResult {
  int get sellerId => throw _privateConstructorUsedError;
  String get shopName => throw _privateConstructorUsedError;

  /// Serializes this SellerInfoResult to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of SellerInfoResult
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SellerInfoResultCopyWith<SellerInfoResult> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SellerInfoResultCopyWith<$Res> {
  factory $SellerInfoResultCopyWith(
    SellerInfoResult value,
    $Res Function(SellerInfoResult) then,
  ) = _$SellerInfoResultCopyWithImpl<$Res, SellerInfoResult>;
  @useResult
  $Res call({int sellerId, String shopName});
}

/// @nodoc
class _$SellerInfoResultCopyWithImpl<$Res, $Val extends SellerInfoResult>
    implements $SellerInfoResultCopyWith<$Res> {
  _$SellerInfoResultCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SellerInfoResult
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({Object? sellerId = null, Object? shopName = null}) {
    return _then(
      _value.copyWith(
            sellerId: null == sellerId
                ? _value.sellerId
                : sellerId // ignore: cast_nullable_to_non_nullable
                      as int,
            shopName: null == shopName
                ? _value.shopName
                : shopName // ignore: cast_nullable_to_non_nullable
                      as String,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$SellerInfoResultImplCopyWith<$Res>
    implements $SellerInfoResultCopyWith<$Res> {
  factory _$$SellerInfoResultImplCopyWith(
    _$SellerInfoResultImpl value,
    $Res Function(_$SellerInfoResultImpl) then,
  ) = __$$SellerInfoResultImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({int sellerId, String shopName});
}

/// @nodoc
class __$$SellerInfoResultImplCopyWithImpl<$Res>
    extends _$SellerInfoResultCopyWithImpl<$Res, _$SellerInfoResultImpl>
    implements _$$SellerInfoResultImplCopyWith<$Res> {
  __$$SellerInfoResultImplCopyWithImpl(
    _$SellerInfoResultImpl _value,
    $Res Function(_$SellerInfoResultImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of SellerInfoResult
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({Object? sellerId = null, Object? shopName = null}) {
    return _then(
      _$SellerInfoResultImpl(
        sellerId: null == sellerId
            ? _value.sellerId
            : sellerId // ignore: cast_nullable_to_non_nullable
                  as int,
        shopName: null == shopName
            ? _value.shopName
            : shopName // ignore: cast_nullable_to_non_nullable
                  as String,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$SellerInfoResultImpl implements _SellerInfoResult {
  const _$SellerInfoResultImpl({
    required this.sellerId,
    required this.shopName,
  });

  factory _$SellerInfoResultImpl.fromJson(Map<String, dynamic> json) =>
      _$$SellerInfoResultImplFromJson(json);

  @override
  final int sellerId;
  @override
  final String shopName;

  @override
  String toString() {
    return 'SellerInfoResult(sellerId: $sellerId, shopName: $shopName)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SellerInfoResultImpl &&
            (identical(other.sellerId, sellerId) ||
                other.sellerId == sellerId) &&
            (identical(other.shopName, shopName) ||
                other.shopName == shopName));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(runtimeType, sellerId, shopName);

  /// Create a copy of SellerInfoResult
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SellerInfoResultImplCopyWith<_$SellerInfoResultImpl> get copyWith =>
      __$$SellerInfoResultImplCopyWithImpl<_$SellerInfoResultImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$SellerInfoResultImplToJson(this);
  }
}

abstract class _SellerInfoResult implements SellerInfoResult {
  const factory _SellerInfoResult({
    required final int sellerId,
    required final String shopName,
  }) = _$SellerInfoResultImpl;

  factory _SellerInfoResult.fromJson(Map<String, dynamic> json) =
      _$SellerInfoResultImpl.fromJson;

  @override
  int get sellerId;
  @override
  String get shopName;

  /// Create a copy of SellerInfoResult
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SellerInfoResultImplCopyWith<_$SellerInfoResultImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
