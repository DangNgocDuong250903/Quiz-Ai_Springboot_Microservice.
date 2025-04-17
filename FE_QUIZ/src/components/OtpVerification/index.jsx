import { message } from "antd";
import { jwtDecode } from "jwt-decode";
import React, { useEffect, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useMutationHook } from "~/hooks/useMutationHook";
import { updateUser } from "~/redux/Slices/userSlice";
import * as AccessService from "~/services/AccessService";
import { IoClose } from "react-icons/io5";
import CustomModal from "~/components/CustomModal";
import Button from "~/components/Button";

const OtpVerification = ({ open, handleClose, userData }) => {
  const navigate = useNavigate();
  const inputRefs = useRef([]);
  const dispatch = useDispatch();
  const [otp, setOtp] = useState(new Array(6).fill(""));
  const [countdown, setCountdown] = useState(0);
  const timerRef = useRef(null);

  useEffect(() => {
    if (inputRefs.current[0]) {
      inputRefs.current[0].focus();
    }
  }, []);

  // Clean up timer on unmount
  useEffect(() => {
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, []);

  const handleChange = (index, event) => {
    const { value } = event.target;
    if (/^[0-9]$/.test(value) || value === "") {
      const newOtp = [...otp];
      newOtp[index] = value;
      setOtp(newOtp);

      if (value && index < inputRefs.current.length - 1) {
        inputRefs.current[index + 1].focus();
      }
    }
  };

  const handleKeyDown = (index, event) => {
    if (event.key === "Backspace" && !otp[index] && index > 0) {
      inputRefs.current[index - 1].focus();
    }
  };

  const mutation = useMutationHook((newData) => AccessService.login(newData));
  const { data, isPending, isSuccess, isError } = mutation;
  const mutationResend = useMutationHook((data) => AccessService.login(data));
  const {
    data: dataResend,
    isPending: isPendingResend,
    isSuccess: isSuccessResend,
    isError: isErrorResend,
  } = mutationResend;

  useEffect(() => {
    if (isPendingResend) {
      message.open({
        type: "loading",
        content: "Gửi OTP tới email của bạn...",
      });
    } else if (isSuccessResend) {
      message.open({
        type: "success",
        content: "Gửi OTP thành công",
      });

      // Start 3-minute countdown (180 seconds)
      setCountdown(180);

      if (timerRef.current) {
        clearInterval(timerRef.current);
      }

      timerRef.current = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timerRef.current);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
  }, [isPendingResend, isSuccessResend]);

  useEffect(() => {
    const fetchUserAndNavigate = async () => {
      if (data?.result?.token) {
        localStorage.setItem("token", data.result.token);
        const decoded = jwtDecode(data.result.token);
        if (decoded?.userId) {
          await handleGetDetailUser({
            id: decoded.userId,
            token: data.result.token,
          });
          navigate("/"); // navigate sau khi update user
        }
      }
    };

    if (isSuccess) {
      fetchUserAndNavigate();
    }
  }, [isSuccess]);

  const handleGetDetailUser = async ({ id, token }) => {
    const res = await AccessService.getDetailUserByUserId({ id });
    dispatch(updateUser({ ...res?.result, token }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const enteredOtp = otp.join("");

    if (enteredOtp.length !== 6) {
      message.destroy();
      message.open({
        type: "warning",
        content: "Vui lòng nhập đầy đủ 6 chữ số OTP",
      });
      return;
    }

    if (userData) {
      const test = { ...userData, otp: Number(enteredOtp) };
      mutation.mutate(test);
    }
  };

  // Format countdown as minutes:seconds
  const formatCountdown = () => {
    const minutes = Math.floor(countdown / 60);
    const seconds = countdown % 60;
    return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
  };

  const handleResendOtp = async () => {
    // Only allow resend if countdown is complete
    if (countdown > 0) return;

    mutationResend.mutate(userData);
  };

  return (
    <CustomModal isOpen={open} onClose={handleClose}>
      <div className="w-full items-end flex justify-end ">
        <div
          className="w-8 h-8 mb-5 active:scale-90 rounded-lg bg-[#065AD8] flex items-center justify-center hover:scale-110 cursor-pointer transition-transform"
          onClick={() => handleClose()}
        >
          <IoClose color="#fff" size={20} />
        </div>
      </div>
      <div className="mx-auto flex w-full max-w-md flex-col space-y-16">
        <div className="flex flex-col items-center justify-center text-center space-y-2">
          <div className="font-semibold text-3xl">
            <p className="text-ascent-1">Xác minh email của bạn</p>
          </div>
          <div className="flex flex-row text-sm font-medium text-ascent-2">
            <p>Chúng tôi đã gửi mã OTP đến email của bạn</p>
          </div>
        </div>

        <div className="w-full flex gap-y-3 flex-col">
          <form onSubmit={handleSubmit}>
            <div className="w-full flex flex-col space-y-16">
              <div className="flex items-center justify-center mx-auto gap-x-2 w-full">
                {otp.map((digit, index) => (
                  <div key={index} className="w-16 h-16">
                    <input
                      ref={(el) => (inputRefs.current[index] = el)}
                      className="w-full h-full flex flex-col items-center text-ascent-1 justify-center bg-primary text-center px-5 outline-none rounded-xl border border-borderNewFeed text-lg font-bold  focus:bg-primary focus:ring-1 ring-blue-700"
                      type="text"
                      maxLength="1"
                      value={digit}
                      onChange={(e) => handleChange(index, e)}
                      onKeyDown={(e) => handleKeyDown(index, e)}
                    />
                  </div>
                ))}
              </div>

              <div className="flex flex-col space-y-5">
                <div className="relative w-full flex items-center justify-center">
                  <Button
                    type="submit"
                    title="Xác minh"
                    disable={isPending}
                    isLoading={isPending}
                    className="flex flex-row hover:opacity-90 transition-transform font-medium items-center justify-center text-center w-full border rounded-xl outline-none py-3 bg-[#065AD8] border-none text-white text-md shadow-sm"
                  />
                </div>
              </div>
            </div>
          </form>
          <div className="flex flex-row items-center justify-center text-center text-sm font-medium space-x-1 text-gray-500">
            <p>Không nhận được mã?</p>
            {countdown > 0 ? (
              <span className="text-blue cursor-default">
                Thử lại sau {formatCountdown()}
              </span>
            ) : (
              <button
                onClick={() => handleResendOtp()}
                disabled={isPendingResend || countdown > 0}
                className="flex flex-row text-blue cursor-pointer items-center text-blue-600 hover:opacity-90"
              >
                Gửi lại mã
              </button>
            )}
          </div>
        </div>
      </div>
    </CustomModal>
  );
};

export default OtpVerification;
