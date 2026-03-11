// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'proposal.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

ProposalSummary _$ProposalSummaryFromJson(Map<String, dynamic> json) {
  return _ProposalSummary.fromJson(json);
}

/// @nodoc
mixin _$ProposalSummary {
  int get proposalId => throw _privateConstructorUsedError;
  String get shopName => throw _privateConstructorUsedError;
  String get conceptTitle => throw _privateConstructorUsedError;
  String get status => throw _privateConstructorUsedError;
  String get expiresAt => throw _privateConstructorUsedError;
  String? get description => throw _privateConstructorUsedError;
  String? get shopDistance => throw _privateConstructorUsedError;
  String? get slotLabel => throw _privateConstructorUsedError;
  String? get mainFlowers => throw _privateConstructorUsedError;
  String? get shopEmoji => throw _privateConstructorUsedError;

  /// Serializes this ProposalSummary to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of ProposalSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $ProposalSummaryCopyWith<ProposalSummary> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ProposalSummaryCopyWith<$Res> {
  factory $ProposalSummaryCopyWith(
    ProposalSummary value,
    $Res Function(ProposalSummary) then,
  ) = _$ProposalSummaryCopyWithImpl<$Res, ProposalSummary>;
  @useResult
  $Res call({
    int proposalId,
    String shopName,
    String conceptTitle,
    String status,
    String expiresAt,
    String? description,
    String? shopDistance,
    String? slotLabel,
    String? mainFlowers,
    String? shopEmoji,
  });
}

/// @nodoc
class _$ProposalSummaryCopyWithImpl<$Res, $Val extends ProposalSummary>
    implements $ProposalSummaryCopyWith<$Res> {
  _$ProposalSummaryCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of ProposalSummary
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? proposalId = null,
    Object? shopName = null,
    Object? conceptTitle = null,
    Object? status = null,
    Object? expiresAt = null,
    Object? description = freezed,
    Object? shopDistance = freezed,
    Object? slotLabel = freezed,
    Object? mainFlowers = freezed,
    Object? shopEmoji = freezed,
  }) {
    return _then(
      _value.copyWith(
            proposalId: null == proposalId
                ? _value.proposalId
                : proposalId // ignore: cast_nullable_to_non_nullable
                      as int,
            shopName: null == shopName
                ? _value.shopName
                : shopName // ignore: cast_nullable_to_non_nullable
                      as String,
            conceptTitle: null == conceptTitle
                ? _value.conceptTitle
                : conceptTitle // ignore: cast_nullable_to_non_nullable
                      as String,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as String,
            expiresAt: null == expiresAt
                ? _value.expiresAt
                : expiresAt // ignore: cast_nullable_to_non_nullable
                      as String,
            description: freezed == description
                ? _value.description
                : description // ignore: cast_nullable_to_non_nullable
                      as String?,
            shopDistance: freezed == shopDistance
                ? _value.shopDistance
                : shopDistance // ignore: cast_nullable_to_non_nullable
                      as String?,
            slotLabel: freezed == slotLabel
                ? _value.slotLabel
                : slotLabel // ignore: cast_nullable_to_non_nullable
                      as String?,
            mainFlowers: freezed == mainFlowers
                ? _value.mainFlowers
                : mainFlowers // ignore: cast_nullable_to_non_nullable
                      as String?,
            shopEmoji: freezed == shopEmoji
                ? _value.shopEmoji
                : shopEmoji // ignore: cast_nullable_to_non_nullable
                      as String?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$ProposalSummaryImplCopyWith<$Res>
    implements $ProposalSummaryCopyWith<$Res> {
  factory _$$ProposalSummaryImplCopyWith(
    _$ProposalSummaryImpl value,
    $Res Function(_$ProposalSummaryImpl) then,
  ) = __$$ProposalSummaryImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int proposalId,
    String shopName,
    String conceptTitle,
    String status,
    String expiresAt,
    String? description,
    String? shopDistance,
    String? slotLabel,
    String? mainFlowers,
    String? shopEmoji,
  });
}

/// @nodoc
class __$$ProposalSummaryImplCopyWithImpl<$Res>
    extends _$ProposalSummaryCopyWithImpl<$Res, _$ProposalSummaryImpl>
    implements _$$ProposalSummaryImplCopyWith<$Res> {
  __$$ProposalSummaryImplCopyWithImpl(
    _$ProposalSummaryImpl _value,
    $Res Function(_$ProposalSummaryImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of ProposalSummary
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? proposalId = null,
    Object? shopName = null,
    Object? conceptTitle = null,
    Object? status = null,
    Object? expiresAt = null,
    Object? description = freezed,
    Object? shopDistance = freezed,
    Object? slotLabel = freezed,
    Object? mainFlowers = freezed,
    Object? shopEmoji = freezed,
  }) {
    return _then(
      _$ProposalSummaryImpl(
        proposalId: null == proposalId
            ? _value.proposalId
            : proposalId // ignore: cast_nullable_to_non_nullable
                  as int,
        shopName: null == shopName
            ? _value.shopName
            : shopName // ignore: cast_nullable_to_non_nullable
                  as String,
        conceptTitle: null == conceptTitle
            ? _value.conceptTitle
            : conceptTitle // ignore: cast_nullable_to_non_nullable
                  as String,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as String,
        expiresAt: null == expiresAt
            ? _value.expiresAt
            : expiresAt // ignore: cast_nullable_to_non_nullable
                  as String,
        description: freezed == description
            ? _value.description
            : description // ignore: cast_nullable_to_non_nullable
                  as String?,
        shopDistance: freezed == shopDistance
            ? _value.shopDistance
            : shopDistance // ignore: cast_nullable_to_non_nullable
                  as String?,
        slotLabel: freezed == slotLabel
            ? _value.slotLabel
            : slotLabel // ignore: cast_nullable_to_non_nullable
                  as String?,
        mainFlowers: freezed == mainFlowers
            ? _value.mainFlowers
            : mainFlowers // ignore: cast_nullable_to_non_nullable
                  as String?,
        shopEmoji: freezed == shopEmoji
            ? _value.shopEmoji
            : shopEmoji // ignore: cast_nullable_to_non_nullable
                  as String?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$ProposalSummaryImpl implements _ProposalSummary {
  const _$ProposalSummaryImpl({
    required this.proposalId,
    required this.shopName,
    required this.conceptTitle,
    required this.status,
    required this.expiresAt,
    this.description,
    this.shopDistance,
    this.slotLabel,
    this.mainFlowers,
    this.shopEmoji,
  });

  factory _$ProposalSummaryImpl.fromJson(Map<String, dynamic> json) =>
      _$$ProposalSummaryImplFromJson(json);

  @override
  final int proposalId;
  @override
  final String shopName;
  @override
  final String conceptTitle;
  @override
  final String status;
  @override
  final String expiresAt;
  @override
  final String? description;
  @override
  final String? shopDistance;
  @override
  final String? slotLabel;
  @override
  final String? mainFlowers;
  @override
  final String? shopEmoji;

  @override
  String toString() {
    return 'ProposalSummary(proposalId: $proposalId, shopName: $shopName, conceptTitle: $conceptTitle, status: $status, expiresAt: $expiresAt, description: $description, shopDistance: $shopDistance, slotLabel: $slotLabel, mainFlowers: $mainFlowers, shopEmoji: $shopEmoji)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ProposalSummaryImpl &&
            (identical(other.proposalId, proposalId) ||
                other.proposalId == proposalId) &&
            (identical(other.shopName, shopName) ||
                other.shopName == shopName) &&
            (identical(other.conceptTitle, conceptTitle) ||
                other.conceptTitle == conceptTitle) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.expiresAt, expiresAt) ||
                other.expiresAt == expiresAt) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.shopDistance, shopDistance) ||
                other.shopDistance == shopDistance) &&
            (identical(other.slotLabel, slotLabel) ||
                other.slotLabel == slotLabel) &&
            (identical(other.mainFlowers, mainFlowers) ||
                other.mainFlowers == mainFlowers) &&
            (identical(other.shopEmoji, shopEmoji) ||
                other.shopEmoji == shopEmoji));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    proposalId,
    shopName,
    conceptTitle,
    status,
    expiresAt,
    description,
    shopDistance,
    slotLabel,
    mainFlowers,
    shopEmoji,
  );

  /// Create a copy of ProposalSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$ProposalSummaryImplCopyWith<_$ProposalSummaryImpl> get copyWith =>
      __$$ProposalSummaryImplCopyWithImpl<_$ProposalSummaryImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$ProposalSummaryImplToJson(this);
  }
}

abstract class _ProposalSummary implements ProposalSummary {
  const factory _ProposalSummary({
    required final int proposalId,
    required final String shopName,
    required final String conceptTitle,
    required final String status,
    required final String expiresAt,
    final String? description,
    final String? shopDistance,
    final String? slotLabel,
    final String? mainFlowers,
    final String? shopEmoji,
  }) = _$ProposalSummaryImpl;

  factory _ProposalSummary.fromJson(Map<String, dynamic> json) =
      _$ProposalSummaryImpl.fromJson;

  @override
  int get proposalId;
  @override
  String get shopName;
  @override
  String get conceptTitle;
  @override
  String get status;
  @override
  String get expiresAt;
  @override
  String? get description;
  @override
  String? get shopDistance;
  @override
  String? get slotLabel;
  @override
  String? get mainFlowers;
  @override
  String? get shopEmoji;

  /// Create a copy of ProposalSummary
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$ProposalSummaryImplCopyWith<_$ProposalSummaryImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

ProposalDetail _$ProposalDetailFromJson(Map<String, dynamic> json) {
  return _ProposalDetail.fromJson(json);
}

/// @nodoc
mixin _$ProposalDetail {
  int get proposalId => throw _privateConstructorUsedError;
  int get requestId => throw _privateConstructorUsedError;
  String get status => throw _privateConstructorUsedError;
  String get shopName => throw _privateConstructorUsedError;
  String get shopAddress => throw _privateConstructorUsedError;
  String get conceptTitle => throw _privateConstructorUsedError;
  String get description => throw _privateConstructorUsedError;
  int get price => throw _privateConstructorUsedError;
  String get expiresAt => throw _privateConstructorUsedError;
  String? get shopDistance => throw _privateConstructorUsedError;
  String? get slotLabel => throw _privateConstructorUsedError;
  String? get shopEmoji => throw _privateConstructorUsedError;
  List<String> get mainFlowers => throw _privateConstructorUsedError;
  List<String> get imageUrls => throw _privateConstructorUsedError;

  /// Serializes this ProposalDetail to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of ProposalDetail
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $ProposalDetailCopyWith<ProposalDetail> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ProposalDetailCopyWith<$Res> {
  factory $ProposalDetailCopyWith(
    ProposalDetail value,
    $Res Function(ProposalDetail) then,
  ) = _$ProposalDetailCopyWithImpl<$Res, ProposalDetail>;
  @useResult
  $Res call({
    int proposalId,
    int requestId,
    String status,
    String shopName,
    String shopAddress,
    String conceptTitle,
    String description,
    int price,
    String expiresAt,
    String? shopDistance,
    String? slotLabel,
    String? shopEmoji,
    List<String> mainFlowers,
    List<String> imageUrls,
  });
}

/// @nodoc
class _$ProposalDetailCopyWithImpl<$Res, $Val extends ProposalDetail>
    implements $ProposalDetailCopyWith<$Res> {
  _$ProposalDetailCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of ProposalDetail
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? proposalId = null,
    Object? requestId = null,
    Object? status = null,
    Object? shopName = null,
    Object? shopAddress = null,
    Object? conceptTitle = null,
    Object? description = null,
    Object? price = null,
    Object? expiresAt = null,
    Object? shopDistance = freezed,
    Object? slotLabel = freezed,
    Object? shopEmoji = freezed,
    Object? mainFlowers = null,
    Object? imageUrls = null,
  }) {
    return _then(
      _value.copyWith(
            proposalId: null == proposalId
                ? _value.proposalId
                : proposalId // ignore: cast_nullable_to_non_nullable
                      as int,
            requestId: null == requestId
                ? _value.requestId
                : requestId // ignore: cast_nullable_to_non_nullable
                      as int,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as String,
            shopName: null == shopName
                ? _value.shopName
                : shopName // ignore: cast_nullable_to_non_nullable
                      as String,
            shopAddress: null == shopAddress
                ? _value.shopAddress
                : shopAddress // ignore: cast_nullable_to_non_nullable
                      as String,
            conceptTitle: null == conceptTitle
                ? _value.conceptTitle
                : conceptTitle // ignore: cast_nullable_to_non_nullable
                      as String,
            description: null == description
                ? _value.description
                : description // ignore: cast_nullable_to_non_nullable
                      as String,
            price: null == price
                ? _value.price
                : price // ignore: cast_nullable_to_non_nullable
                      as int,
            expiresAt: null == expiresAt
                ? _value.expiresAt
                : expiresAt // ignore: cast_nullable_to_non_nullable
                      as String,
            shopDistance: freezed == shopDistance
                ? _value.shopDistance
                : shopDistance // ignore: cast_nullable_to_non_nullable
                      as String?,
            slotLabel: freezed == slotLabel
                ? _value.slotLabel
                : slotLabel // ignore: cast_nullable_to_non_nullable
                      as String?,
            shopEmoji: freezed == shopEmoji
                ? _value.shopEmoji
                : shopEmoji // ignore: cast_nullable_to_non_nullable
                      as String?,
            mainFlowers: null == mainFlowers
                ? _value.mainFlowers
                : mainFlowers // ignore: cast_nullable_to_non_nullable
                      as List<String>,
            imageUrls: null == imageUrls
                ? _value.imageUrls
                : imageUrls // ignore: cast_nullable_to_non_nullable
                      as List<String>,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$ProposalDetailImplCopyWith<$Res>
    implements $ProposalDetailCopyWith<$Res> {
  factory _$$ProposalDetailImplCopyWith(
    _$ProposalDetailImpl value,
    $Res Function(_$ProposalDetailImpl) then,
  ) = __$$ProposalDetailImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int proposalId,
    int requestId,
    String status,
    String shopName,
    String shopAddress,
    String conceptTitle,
    String description,
    int price,
    String expiresAt,
    String? shopDistance,
    String? slotLabel,
    String? shopEmoji,
    List<String> mainFlowers,
    List<String> imageUrls,
  });
}

/// @nodoc
class __$$ProposalDetailImplCopyWithImpl<$Res>
    extends _$ProposalDetailCopyWithImpl<$Res, _$ProposalDetailImpl>
    implements _$$ProposalDetailImplCopyWith<$Res> {
  __$$ProposalDetailImplCopyWithImpl(
    _$ProposalDetailImpl _value,
    $Res Function(_$ProposalDetailImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of ProposalDetail
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? proposalId = null,
    Object? requestId = null,
    Object? status = null,
    Object? shopName = null,
    Object? shopAddress = null,
    Object? conceptTitle = null,
    Object? description = null,
    Object? price = null,
    Object? expiresAt = null,
    Object? shopDistance = freezed,
    Object? slotLabel = freezed,
    Object? shopEmoji = freezed,
    Object? mainFlowers = null,
    Object? imageUrls = null,
  }) {
    return _then(
      _$ProposalDetailImpl(
        proposalId: null == proposalId
            ? _value.proposalId
            : proposalId // ignore: cast_nullable_to_non_nullable
                  as int,
        requestId: null == requestId
            ? _value.requestId
            : requestId // ignore: cast_nullable_to_non_nullable
                  as int,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as String,
        shopName: null == shopName
            ? _value.shopName
            : shopName // ignore: cast_nullable_to_non_nullable
                  as String,
        shopAddress: null == shopAddress
            ? _value.shopAddress
            : shopAddress // ignore: cast_nullable_to_non_nullable
                  as String,
        conceptTitle: null == conceptTitle
            ? _value.conceptTitle
            : conceptTitle // ignore: cast_nullable_to_non_nullable
                  as String,
        description: null == description
            ? _value.description
            : description // ignore: cast_nullable_to_non_nullable
                  as String,
        price: null == price
            ? _value.price
            : price // ignore: cast_nullable_to_non_nullable
                  as int,
        expiresAt: null == expiresAt
            ? _value.expiresAt
            : expiresAt // ignore: cast_nullable_to_non_nullable
                  as String,
        shopDistance: freezed == shopDistance
            ? _value.shopDistance
            : shopDistance // ignore: cast_nullable_to_non_nullable
                  as String?,
        slotLabel: freezed == slotLabel
            ? _value.slotLabel
            : slotLabel // ignore: cast_nullable_to_non_nullable
                  as String?,
        shopEmoji: freezed == shopEmoji
            ? _value.shopEmoji
            : shopEmoji // ignore: cast_nullable_to_non_nullable
                  as String?,
        mainFlowers: null == mainFlowers
            ? _value._mainFlowers
            : mainFlowers // ignore: cast_nullable_to_non_nullable
                  as List<String>,
        imageUrls: null == imageUrls
            ? _value._imageUrls
            : imageUrls // ignore: cast_nullable_to_non_nullable
                  as List<String>,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$ProposalDetailImpl implements _ProposalDetail {
  const _$ProposalDetailImpl({
    required this.proposalId,
    required this.requestId,
    required this.status,
    required this.shopName,
    required this.shopAddress,
    required this.conceptTitle,
    required this.description,
    required this.price,
    required this.expiresAt,
    this.shopDistance,
    this.slotLabel,
    this.shopEmoji,
    final List<String> mainFlowers = const [],
    final List<String> imageUrls = const [],
  }) : _mainFlowers = mainFlowers,
       _imageUrls = imageUrls;

  factory _$ProposalDetailImpl.fromJson(Map<String, dynamic> json) =>
      _$$ProposalDetailImplFromJson(json);

  @override
  final int proposalId;
  @override
  final int requestId;
  @override
  final String status;
  @override
  final String shopName;
  @override
  final String shopAddress;
  @override
  final String conceptTitle;
  @override
  final String description;
  @override
  final int price;
  @override
  final String expiresAt;
  @override
  final String? shopDistance;
  @override
  final String? slotLabel;
  @override
  final String? shopEmoji;
  final List<String> _mainFlowers;
  @override
  @JsonKey()
  List<String> get mainFlowers {
    if (_mainFlowers is EqualUnmodifiableListView) return _mainFlowers;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_mainFlowers);
  }

  final List<String> _imageUrls;
  @override
  @JsonKey()
  List<String> get imageUrls {
    if (_imageUrls is EqualUnmodifiableListView) return _imageUrls;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_imageUrls);
  }

  @override
  String toString() {
    return 'ProposalDetail(proposalId: $proposalId, requestId: $requestId, status: $status, shopName: $shopName, shopAddress: $shopAddress, conceptTitle: $conceptTitle, description: $description, price: $price, expiresAt: $expiresAt, shopDistance: $shopDistance, slotLabel: $slotLabel, shopEmoji: $shopEmoji, mainFlowers: $mainFlowers, imageUrls: $imageUrls)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ProposalDetailImpl &&
            (identical(other.proposalId, proposalId) ||
                other.proposalId == proposalId) &&
            (identical(other.requestId, requestId) ||
                other.requestId == requestId) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.shopName, shopName) ||
                other.shopName == shopName) &&
            (identical(other.shopAddress, shopAddress) ||
                other.shopAddress == shopAddress) &&
            (identical(other.conceptTitle, conceptTitle) ||
                other.conceptTitle == conceptTitle) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.price, price) || other.price == price) &&
            (identical(other.expiresAt, expiresAt) ||
                other.expiresAt == expiresAt) &&
            (identical(other.shopDistance, shopDistance) ||
                other.shopDistance == shopDistance) &&
            (identical(other.slotLabel, slotLabel) ||
                other.slotLabel == slotLabel) &&
            (identical(other.shopEmoji, shopEmoji) ||
                other.shopEmoji == shopEmoji) &&
            const DeepCollectionEquality().equals(
              other._mainFlowers,
              _mainFlowers,
            ) &&
            const DeepCollectionEquality().equals(
              other._imageUrls,
              _imageUrls,
            ));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    proposalId,
    requestId,
    status,
    shopName,
    shopAddress,
    conceptTitle,
    description,
    price,
    expiresAt,
    shopDistance,
    slotLabel,
    shopEmoji,
    const DeepCollectionEquality().hash(_mainFlowers),
    const DeepCollectionEquality().hash(_imageUrls),
  );

  /// Create a copy of ProposalDetail
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$ProposalDetailImplCopyWith<_$ProposalDetailImpl> get copyWith =>
      __$$ProposalDetailImplCopyWithImpl<_$ProposalDetailImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$ProposalDetailImplToJson(this);
  }
}

abstract class _ProposalDetail implements ProposalDetail {
  const factory _ProposalDetail({
    required final int proposalId,
    required final int requestId,
    required final String status,
    required final String shopName,
    required final String shopAddress,
    required final String conceptTitle,
    required final String description,
    required final int price,
    required final String expiresAt,
    final String? shopDistance,
    final String? slotLabel,
    final String? shopEmoji,
    final List<String> mainFlowers,
    final List<String> imageUrls,
  }) = _$ProposalDetailImpl;

  factory _ProposalDetail.fromJson(Map<String, dynamic> json) =
      _$ProposalDetailImpl.fromJson;

  @override
  int get proposalId;
  @override
  int get requestId;
  @override
  String get status;
  @override
  String get shopName;
  @override
  String get shopAddress;
  @override
  String get conceptTitle;
  @override
  String get description;
  @override
  int get price;
  @override
  String get expiresAt;
  @override
  String? get shopDistance;
  @override
  String? get slotLabel;
  @override
  String? get shopEmoji;
  @override
  List<String> get mainFlowers;
  @override
  List<String> get imageUrls;

  /// Create a copy of ProposalDetail
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$ProposalDetailImplCopyWith<_$ProposalDetailImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

ReservationDetail _$ReservationDetailFromJson(Map<String, dynamic> json) {
  return _ReservationDetail.fromJson(json);
}

/// @nodoc
mixin _$ReservationDetail {
  int get reservationId => throw _privateConstructorUsedError;
  String get status => throw _privateConstructorUsedError;
  String get shopName => throw _privateConstructorUsedError;
  String get shopAddress => throw _privateConstructorUsedError;
  String get conceptTitle => throw _privateConstructorUsedError;
  int get price => throw _privateConstructorUsedError;
  String get fulfillmentType => throw _privateConstructorUsedError;
  String get fulfillmentDate => throw _privateConstructorUsedError;
  String get slotLabel => throw _privateConstructorUsedError;
  String? get shopEmoji => throw _privateConstructorUsedError;

  /// Serializes this ReservationDetail to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of ReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $ReservationDetailCopyWith<ReservationDetail> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ReservationDetailCopyWith<$Res> {
  factory $ReservationDetailCopyWith(
    ReservationDetail value,
    $Res Function(ReservationDetail) then,
  ) = _$ReservationDetailCopyWithImpl<$Res, ReservationDetail>;
  @useResult
  $Res call({
    int reservationId,
    String status,
    String shopName,
    String shopAddress,
    String conceptTitle,
    int price,
    String fulfillmentType,
    String fulfillmentDate,
    String slotLabel,
    String? shopEmoji,
  });
}

/// @nodoc
class _$ReservationDetailCopyWithImpl<$Res, $Val extends ReservationDetail>
    implements $ReservationDetailCopyWith<$Res> {
  _$ReservationDetailCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of ReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? reservationId = null,
    Object? status = null,
    Object? shopName = null,
    Object? shopAddress = null,
    Object? conceptTitle = null,
    Object? price = null,
    Object? fulfillmentType = null,
    Object? fulfillmentDate = null,
    Object? slotLabel = null,
    Object? shopEmoji = freezed,
  }) {
    return _then(
      _value.copyWith(
            reservationId: null == reservationId
                ? _value.reservationId
                : reservationId // ignore: cast_nullable_to_non_nullable
                      as int,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as String,
            shopName: null == shopName
                ? _value.shopName
                : shopName // ignore: cast_nullable_to_non_nullable
                      as String,
            shopAddress: null == shopAddress
                ? _value.shopAddress
                : shopAddress // ignore: cast_nullable_to_non_nullable
                      as String,
            conceptTitle: null == conceptTitle
                ? _value.conceptTitle
                : conceptTitle // ignore: cast_nullable_to_non_nullable
                      as String,
            price: null == price
                ? _value.price
                : price // ignore: cast_nullable_to_non_nullable
                      as int,
            fulfillmentType: null == fulfillmentType
                ? _value.fulfillmentType
                : fulfillmentType // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentDate: null == fulfillmentDate
                ? _value.fulfillmentDate
                : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                      as String,
            slotLabel: null == slotLabel
                ? _value.slotLabel
                : slotLabel // ignore: cast_nullable_to_non_nullable
                      as String,
            shopEmoji: freezed == shopEmoji
                ? _value.shopEmoji
                : shopEmoji // ignore: cast_nullable_to_non_nullable
                      as String?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$ReservationDetailImplCopyWith<$Res>
    implements $ReservationDetailCopyWith<$Res> {
  factory _$$ReservationDetailImplCopyWith(
    _$ReservationDetailImpl value,
    $Res Function(_$ReservationDetailImpl) then,
  ) = __$$ReservationDetailImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int reservationId,
    String status,
    String shopName,
    String shopAddress,
    String conceptTitle,
    int price,
    String fulfillmentType,
    String fulfillmentDate,
    String slotLabel,
    String? shopEmoji,
  });
}

/// @nodoc
class __$$ReservationDetailImplCopyWithImpl<$Res>
    extends _$ReservationDetailCopyWithImpl<$Res, _$ReservationDetailImpl>
    implements _$$ReservationDetailImplCopyWith<$Res> {
  __$$ReservationDetailImplCopyWithImpl(
    _$ReservationDetailImpl _value,
    $Res Function(_$ReservationDetailImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of ReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? reservationId = null,
    Object? status = null,
    Object? shopName = null,
    Object? shopAddress = null,
    Object? conceptTitle = null,
    Object? price = null,
    Object? fulfillmentType = null,
    Object? fulfillmentDate = null,
    Object? slotLabel = null,
    Object? shopEmoji = freezed,
  }) {
    return _then(
      _$ReservationDetailImpl(
        reservationId: null == reservationId
            ? _value.reservationId
            : reservationId // ignore: cast_nullable_to_non_nullable
                  as int,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as String,
        shopName: null == shopName
            ? _value.shopName
            : shopName // ignore: cast_nullable_to_non_nullable
                  as String,
        shopAddress: null == shopAddress
            ? _value.shopAddress
            : shopAddress // ignore: cast_nullable_to_non_nullable
                  as String,
        conceptTitle: null == conceptTitle
            ? _value.conceptTitle
            : conceptTitle // ignore: cast_nullable_to_non_nullable
                  as String,
        price: null == price
            ? _value.price
            : price // ignore: cast_nullable_to_non_nullable
                  as int,
        fulfillmentType: null == fulfillmentType
            ? _value.fulfillmentType
            : fulfillmentType // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentDate: null == fulfillmentDate
            ? _value.fulfillmentDate
            : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                  as String,
        slotLabel: null == slotLabel
            ? _value.slotLabel
            : slotLabel // ignore: cast_nullable_to_non_nullable
                  as String,
        shopEmoji: freezed == shopEmoji
            ? _value.shopEmoji
            : shopEmoji // ignore: cast_nullable_to_non_nullable
                  as String?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$ReservationDetailImpl implements _ReservationDetail {
  const _$ReservationDetailImpl({
    required this.reservationId,
    required this.status,
    required this.shopName,
    required this.shopAddress,
    required this.conceptTitle,
    required this.price,
    required this.fulfillmentType,
    required this.fulfillmentDate,
    required this.slotLabel,
    this.shopEmoji,
  });

  factory _$ReservationDetailImpl.fromJson(Map<String, dynamic> json) =>
      _$$ReservationDetailImplFromJson(json);

  @override
  final int reservationId;
  @override
  final String status;
  @override
  final String shopName;
  @override
  final String shopAddress;
  @override
  final String conceptTitle;
  @override
  final int price;
  @override
  final String fulfillmentType;
  @override
  final String fulfillmentDate;
  @override
  final String slotLabel;
  @override
  final String? shopEmoji;

  @override
  String toString() {
    return 'ReservationDetail(reservationId: $reservationId, status: $status, shopName: $shopName, shopAddress: $shopAddress, conceptTitle: $conceptTitle, price: $price, fulfillmentType: $fulfillmentType, fulfillmentDate: $fulfillmentDate, slotLabel: $slotLabel, shopEmoji: $shopEmoji)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ReservationDetailImpl &&
            (identical(other.reservationId, reservationId) ||
                other.reservationId == reservationId) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.shopName, shopName) ||
                other.shopName == shopName) &&
            (identical(other.shopAddress, shopAddress) ||
                other.shopAddress == shopAddress) &&
            (identical(other.conceptTitle, conceptTitle) ||
                other.conceptTitle == conceptTitle) &&
            (identical(other.price, price) || other.price == price) &&
            (identical(other.fulfillmentType, fulfillmentType) ||
                other.fulfillmentType == fulfillmentType) &&
            (identical(other.fulfillmentDate, fulfillmentDate) ||
                other.fulfillmentDate == fulfillmentDate) &&
            (identical(other.slotLabel, slotLabel) ||
                other.slotLabel == slotLabel) &&
            (identical(other.shopEmoji, shopEmoji) ||
                other.shopEmoji == shopEmoji));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    reservationId,
    status,
    shopName,
    shopAddress,
    conceptTitle,
    price,
    fulfillmentType,
    fulfillmentDate,
    slotLabel,
    shopEmoji,
  );

  /// Create a copy of ReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$ReservationDetailImplCopyWith<_$ReservationDetailImpl> get copyWith =>
      __$$ReservationDetailImplCopyWithImpl<_$ReservationDetailImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$ReservationDetailImplToJson(this);
  }
}

abstract class _ReservationDetail implements ReservationDetail {
  const factory _ReservationDetail({
    required final int reservationId,
    required final String status,
    required final String shopName,
    required final String shopAddress,
    required final String conceptTitle,
    required final int price,
    required final String fulfillmentType,
    required final String fulfillmentDate,
    required final String slotLabel,
    final String? shopEmoji,
  }) = _$ReservationDetailImpl;

  factory _ReservationDetail.fromJson(Map<String, dynamic> json) =
      _$ReservationDetailImpl.fromJson;

  @override
  int get reservationId;
  @override
  String get status;
  @override
  String get shopName;
  @override
  String get shopAddress;
  @override
  String get conceptTitle;
  @override
  int get price;
  @override
  String get fulfillmentType;
  @override
  String get fulfillmentDate;
  @override
  String get slotLabel;
  @override
  String? get shopEmoji;

  /// Create a copy of ReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$ReservationDetailImplCopyWith<_$ReservationDetailImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

NotificationItem _$NotificationItemFromJson(Map<String, dynamic> json) {
  return _NotificationItem.fromJson(json);
}

/// @nodoc
mixin _$NotificationItem {
  int get notificationId => throw _privateConstructorUsedError;
  String get type => throw _privateConstructorUsedError;
  String get title => throw _privateConstructorUsedError;
  String get body => throw _privateConstructorUsedError;
  String get createdAt => throw _privateConstructorUsedError;
  bool get isRead => throw _privateConstructorUsedError;
  String? get referenceType => throw _privateConstructorUsedError;
  int? get referenceId => throw _privateConstructorUsedError;
  int? get proposalId => throw _privateConstructorUsedError;

  /// Serializes this NotificationItem to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of NotificationItem
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $NotificationItemCopyWith<NotificationItem> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $NotificationItemCopyWith<$Res> {
  factory $NotificationItemCopyWith(
    NotificationItem value,
    $Res Function(NotificationItem) then,
  ) = _$NotificationItemCopyWithImpl<$Res, NotificationItem>;
  @useResult
  $Res call({
    int notificationId,
    String type,
    String title,
    String body,
    String createdAt,
    bool isRead,
    String? referenceType,
    int? referenceId,
    int? proposalId,
  });
}

/// @nodoc
class _$NotificationItemCopyWithImpl<$Res, $Val extends NotificationItem>
    implements $NotificationItemCopyWith<$Res> {
  _$NotificationItemCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of NotificationItem
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? notificationId = null,
    Object? type = null,
    Object? title = null,
    Object? body = null,
    Object? createdAt = null,
    Object? isRead = null,
    Object? referenceType = freezed,
    Object? referenceId = freezed,
    Object? proposalId = freezed,
  }) {
    return _then(
      _value.copyWith(
            notificationId: null == notificationId
                ? _value.notificationId
                : notificationId // ignore: cast_nullable_to_non_nullable
                      as int,
            type: null == type
                ? _value.type
                : type // ignore: cast_nullable_to_non_nullable
                      as String,
            title: null == title
                ? _value.title
                : title // ignore: cast_nullable_to_non_nullable
                      as String,
            body: null == body
                ? _value.body
                : body // ignore: cast_nullable_to_non_nullable
                      as String,
            createdAt: null == createdAt
                ? _value.createdAt
                : createdAt // ignore: cast_nullable_to_non_nullable
                      as String,
            isRead: null == isRead
                ? _value.isRead
                : isRead // ignore: cast_nullable_to_non_nullable
                      as bool,
            referenceType: freezed == referenceType
                ? _value.referenceType
                : referenceType // ignore: cast_nullable_to_non_nullable
                      as String?,
            referenceId: freezed == referenceId
                ? _value.referenceId
                : referenceId // ignore: cast_nullable_to_non_nullable
                      as int?,
            proposalId: freezed == proposalId
                ? _value.proposalId
                : proposalId // ignore: cast_nullable_to_non_nullable
                      as int?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$NotificationItemImplCopyWith<$Res>
    implements $NotificationItemCopyWith<$Res> {
  factory _$$NotificationItemImplCopyWith(
    _$NotificationItemImpl value,
    $Res Function(_$NotificationItemImpl) then,
  ) = __$$NotificationItemImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int notificationId,
    String type,
    String title,
    String body,
    String createdAt,
    bool isRead,
    String? referenceType,
    int? referenceId,
    int? proposalId,
  });
}

/// @nodoc
class __$$NotificationItemImplCopyWithImpl<$Res>
    extends _$NotificationItemCopyWithImpl<$Res, _$NotificationItemImpl>
    implements _$$NotificationItemImplCopyWith<$Res> {
  __$$NotificationItemImplCopyWithImpl(
    _$NotificationItemImpl _value,
    $Res Function(_$NotificationItemImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of NotificationItem
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? notificationId = null,
    Object? type = null,
    Object? title = null,
    Object? body = null,
    Object? createdAt = null,
    Object? isRead = null,
    Object? referenceType = freezed,
    Object? referenceId = freezed,
    Object? proposalId = freezed,
  }) {
    return _then(
      _$NotificationItemImpl(
        notificationId: null == notificationId
            ? _value.notificationId
            : notificationId // ignore: cast_nullable_to_non_nullable
                  as int,
        type: null == type
            ? _value.type
            : type // ignore: cast_nullable_to_non_nullable
                  as String,
        title: null == title
            ? _value.title
            : title // ignore: cast_nullable_to_non_nullable
                  as String,
        body: null == body
            ? _value.body
            : body // ignore: cast_nullable_to_non_nullable
                  as String,
        createdAt: null == createdAt
            ? _value.createdAt
            : createdAt // ignore: cast_nullable_to_non_nullable
                  as String,
        isRead: null == isRead
            ? _value.isRead
            : isRead // ignore: cast_nullable_to_non_nullable
                  as bool,
        referenceType: freezed == referenceType
            ? _value.referenceType
            : referenceType // ignore: cast_nullable_to_non_nullable
                  as String?,
        referenceId: freezed == referenceId
            ? _value.referenceId
            : referenceId // ignore: cast_nullable_to_non_nullable
                  as int?,
        proposalId: freezed == proposalId
            ? _value.proposalId
            : proposalId // ignore: cast_nullable_to_non_nullable
                  as int?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$NotificationItemImpl implements _NotificationItem {
  const _$NotificationItemImpl({
    required this.notificationId,
    required this.type,
    required this.title,
    required this.body,
    required this.createdAt,
    required this.isRead,
    this.referenceType,
    this.referenceId,
    this.proposalId,
  });

  factory _$NotificationItemImpl.fromJson(Map<String, dynamic> json) =>
      _$$NotificationItemImplFromJson(json);

  @override
  final int notificationId;
  @override
  final String type;
  @override
  final String title;
  @override
  final String body;
  @override
  final String createdAt;
  @override
  final bool isRead;
  @override
  final String? referenceType;
  @override
  final int? referenceId;
  @override
  final int? proposalId;

  @override
  String toString() {
    return 'NotificationItem(notificationId: $notificationId, type: $type, title: $title, body: $body, createdAt: $createdAt, isRead: $isRead, referenceType: $referenceType, referenceId: $referenceId, proposalId: $proposalId)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$NotificationItemImpl &&
            (identical(other.notificationId, notificationId) ||
                other.notificationId == notificationId) &&
            (identical(other.type, type) || other.type == type) &&
            (identical(other.title, title) || other.title == title) &&
            (identical(other.body, body) || other.body == body) &&
            (identical(other.createdAt, createdAt) ||
                other.createdAt == createdAt) &&
            (identical(other.isRead, isRead) || other.isRead == isRead) &&
            (identical(other.referenceType, referenceType) ||
                other.referenceType == referenceType) &&
            (identical(other.referenceId, referenceId) ||
                other.referenceId == referenceId) &&
            (identical(other.proposalId, proposalId) ||
                other.proposalId == proposalId));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    notificationId,
    type,
    title,
    body,
    createdAt,
    isRead,
    referenceType,
    referenceId,
    proposalId,
  );

  /// Create a copy of NotificationItem
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$NotificationItemImplCopyWith<_$NotificationItemImpl> get copyWith =>
      __$$NotificationItemImplCopyWithImpl<_$NotificationItemImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$NotificationItemImplToJson(this);
  }
}

abstract class _NotificationItem implements NotificationItem {
  const factory _NotificationItem({
    required final int notificationId,
    required final String type,
    required final String title,
    required final String body,
    required final String createdAt,
    required final bool isRead,
    final String? referenceType,
    final int? referenceId,
    final int? proposalId,
  }) = _$NotificationItemImpl;

  factory _NotificationItem.fromJson(Map<String, dynamic> json) =
      _$NotificationItemImpl.fromJson;

  @override
  int get notificationId;
  @override
  String get type;
  @override
  String get title;
  @override
  String get body;
  @override
  String get createdAt;
  @override
  bool get isRead;
  @override
  String? get referenceType;
  @override
  int? get referenceId;
  @override
  int? get proposalId;

  /// Create a copy of NotificationItem
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$NotificationItemImplCopyWith<_$NotificationItemImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
