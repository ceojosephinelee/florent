import 'dart:async';

import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/typography.dart';

class ExpiryTimer extends StatefulWidget {
  const ExpiryTimer({super.key, required this.expiresAt});

  final DateTime expiresAt;

  @override
  State<ExpiryTimer> createState() => _ExpiryTimerState();
}

class _ExpiryTimerState extends State<ExpiryTimer> {
  late Timer _timer;
  late Duration _remaining;

  @override
  void initState() {
    super.initState();
    _remaining = widget.expiresAt.difference(DateTime.now());
    _timer = Timer.periodic(const Duration(seconds: 1), (_) {
      setState(() {
        _remaining = widget.expiresAt.difference(DateTime.now());
        if (_remaining.isNegative) {
          _remaining = Duration.zero;
          _timer.cancel();
        }
      });
    });
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }

  String _format(Duration d) {
    final h = d.inHours.toString().padLeft(2, '0');
    final m = (d.inMinutes % 60).toString().padLeft(2, '0');
    final s = (d.inSeconds % 60).toString().padLeft(2, '0');
    return '$h:$m:$s';
  }

  @override
  Widget build(BuildContext context) {
    final isUrgent = _remaining.inHours < 6;
    return Text(
      _format(_remaining),
      style: AppTypography.mono(
        fontSize: 13,
        fontWeight: FontWeight.w500,
        color: isUrgent ? roseColor : ink60,
      ),
    );
  }
}
